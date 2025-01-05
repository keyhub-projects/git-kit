/*
 * MIT License
 *
 * Copyright (c) 2024 KH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package keyhub.gitkit.core.local;

import keyhub.gitkit.core.IllegalGitStateException;
import keyhub.gitkit.core.annotation.SimpleAbstractGitOperationAspect;
import keyhub.gitkit.core.value.CommitFileDiff;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SimpleLocalGit implements LocalGit {

	private final SimpleAbstractGitOperationAspect aspect;

	SimpleAbstractGitOperationAspect aspect() {
		return this.aspect;
	}

	private SimpleLocalGit() {
		this.aspect = new SimpleAbstractGitOperationAspect();
	}

	static SimpleLocalGit init() {
		return new SimpleLocalGit();
	}

	@Override
	public Optional<RevCommit> findCommitByHash(Git git, String hash) {
		try (RevWalk revWalk = new RevWalk(git.getRepository())) {
			ObjectId headId = git.getRepository().resolve("HEAD");
			revWalk.markStart(revWalk.parseCommit(headId));
			for (RevCommit commit : revWalk) {
				if (commit.getName().startsWith(hash)) {
					return Optional.of(commit);
				}
			}
			return Optional.empty();
		} catch (IOException e) {
			throw new IllegalGitStateException(e);
		}
	}

	@Override
	public List<DiffEntry> findDiffs(Git git, RevCommit oldCommit, RevCommit newCommit) {
		Repository repository = git.getRepository();
		try (var reader = repository.newObjectReader()) {
			CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();

			oldTreeParser.reset(reader, oldCommit.getTree());

			CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
			newTreeParser.reset(reader, newCommit.getTree());

			return new Git(repository).diff()
				.setOldTree(oldTreeParser)
				.setNewTree(newTreeParser)
				.call();
		} catch (IOException | GitAPIException e) {
			throw new IllegalGitStateException(e);
		}
	}

	@Override
	public List<DiffEntry> findDiffMappers(Git git, RevCommit oldCommit, RevCommit newCommit) {
		List<DiffEntry> list = findDiffs(git, oldCommit, newCommit);
		List<DiffEntry> result = new ArrayList<>();
		for (DiffEntry diff : list) {
			if (diff.getNewPath().endsWith("Mapper.java")) {
				String fileContent = readFileContents(git, diff.getNewId().toObjectId());
				if (fileContent.contains("@Mapper")
					&& !fileContent.contains("package kr.co.milkt.nextgen.deltapatcher")) {
					result.add(diff);
				}
			}
		}
		return result;
	}

	@Override
	public List<CommitFileDiff> findDiffFiles(Git git, RevCommit oldCommit, RevCommit newCommit) {
		List<DiffEntry> diffEntries = findDiffs(git, oldCommit, newCommit);
		return diffEntries.stream()
			.map(diffEntry -> CommitFileDiff.of(
					diffEntry,
					oldCommit,
					readFileContents(git, diffEntry.getOldId().toObjectId()),
					newCommit,
					readFileContents(git, diffEntry.getNewId().toObjectId())
				)
			)
			.toList();
	}

	@Override
	public List<CommitFileDiff> findDiffMapperFiles(Git git, RevCommit oldCommit, RevCommit newCommit) {
		List<DiffEntry> diffEntries = findDiffMappers(git, oldCommit, newCommit);
		return diffEntries.stream()
			.map(diffEntry -> CommitFileDiff.of(
					diffEntry,
					oldCommit,
					readFileContents(git, diffEntry.getOldId().toObjectId()),
					newCommit,
					readFileContents(git, diffEntry.getNewId().toObjectId())
				)
			)
			.toList();
	}

	@Override
	public String readFileContents(Git git, ObjectId id) {
		try {
			byte[] contents = git.getRepository()
				.open(id)
				.getBytes();
			return convertByteContentsToString(contents);
		} catch (IOException e) {
			throw new IllegalGitStateException(e);
		}
	}

	private String convertByteContentsToString(byte[] contents) {
		return new String(contents, StandardCharsets.UTF_8);
	}

	@Override
	public int stageAll(Git git, String path) {
		try {
			DirCache result = git.add()
				.addFilepattern(path)
				.call();
			return result.getEntryCount();
		} catch (GitAPIException e) {
			throw new IllegalGitStateException(e);
		}
	}

	@Override
	public String commit(Git git, String commitMessage) {
		try {
			RevCommit result = git.commit()
				.setMessage(commitMessage)
				.call();
			return result.getName();
		} catch (GitAPIException e) {
			throw new IllegalGitStateException(e);
		}
	}

	@Override
	public String reset(Git git, ResetCommand.ResetType mode) {
		try {
			Ref result = git.reset()
				.setMode(mode)
				.call();
			return result.getName();
		} catch (GitAPIException e) {
			throw new IllegalGitStateException(e);
		}
	}

	@Override
	public boolean isConflicted(Git git) {
		Repository repository = git.getRepository();
		return repository.getRepositoryState().equals(RepositoryState.MERGING);
	}

	@Override
	public int resolveConflicts(Git git, CheckoutCommand.Stage side){
		try {
			int result = 0;
			Repository repository = git.getRepository();
			Set<String> conflicts = git.status().call().getConflicting();
			if (!conflicts.isEmpty()) {
				for (String conflict : conflicts) {
					git.checkout()
						.setStage(side)
						.addPath(conflict)
						.call();
					result++;
				}
			}
			git.commit()
				.setMessage("Resolved merge conflicts with " + side.name())
				.call();
			return result;
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}
}
