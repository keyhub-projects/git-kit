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

package keyhub.gitkit.starter.service.diff;

import keyhub.gitkit.core.IllegalGitStateException;
import keyhub.gitkit.core.annotation.GitOperation;
import keyhub.gitkit.core.entity.DiffCommit;
import keyhub.gitkit.core.entity.CommitHistory;
import keyhub.gitkit.core.local.LocalGit;
import keyhub.gitkit.core.value.CommitFileDiff;
import keyhub.gitkit.starter.entity.DiffCommitEntity;
import keyhub.gitkit.starter.entity.CommitHistoryEntity;
import keyhub.gitkit.starter.entity.DiffQueryCommitEntity;
import keyhub.gitkit.starter.persistence.CommitRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DiffService {
	private final LocalGit localGit;
	private final CommitRepository<Long> repository;
	@Value("${git.repo-path}")
	private String gitRepoPath;

	@GitOperation(repoPath = "gitRepoPath", repoPathFrom = GitOperation.RepoPathFrom.FIELD)
	@Transactional
	public int saveDiffQueries(String oldId, String newId) {
		RevCommit oldCommit = getCommitByHash(oldId);
		RevCommit newCommit = getCommitByHash(newId);
		List<CommitFileDiff> diffMappers = localGit.findDiffMapperFiles(oldCommit, newCommit);
		int result = 0;
		for (CommitFileDiff fileDiffDto : diffMappers) {
			CommitHistory<Long> commitHistory = CommitHistoryEntity.of(fileDiffDto);
			Optional<CommitHistory<Long>> optionalComparingCommitHistory = repository.findByCommitPoint(
					fileDiffDto.oldFile().commitHash());
			List<DiffQueryCommitEntity> diffs = optionalComparingCommitHistory
					.map(commitHistoryEntity -> DiffQueryCommitEntity.ofAll(fileDiffDto, commitHistory))
					.orElseGet(List::of);
			commitHistory.addAll(diffs);
			repository.save(commitHistory);
			result += diffs.size();
		}
		return result;
	}

	private RevCommit getCommitByHash(String hash) {
		return localGit.findCommitByHash(hash)
			.orElseThrow(IllegalGitStateException::new);
	}

	@GitOperation(repoPath = "gitRepoPath", repoPathFrom = GitOperation.RepoPathFrom.FIELD)
	@Transactional
	public int saveDiffs(String oldId, String newId) {
		RevCommit oldCommit = getCommitByHash(oldId);
		RevCommit newCommit = getCommitByHash(newId);
		List<CommitFileDiff> diffMappers = localGit.findDiffMapperFiles(oldCommit, newCommit);
		int result = 0;
		for (CommitFileDiff fileDiffDto : diffMappers) {
			CommitHistory<Long> commitHistory = CommitHistoryEntity.of(fileDiffDto);
			Optional<CommitHistory<Long>> optionalComparingCommitHistory = repository.findByCommitPoint(
				fileDiffDto.oldFile().commitHash());
			DiffCommit<Long> diff = optionalComparingCommitHistory
				.map(commitHistoryEntity -> DiffCommitEntity.of(fileDiffDto, commitHistory))
				.orElse(null);
			if(diff == null){
				continue;
			}
			commitHistory.add(diff);
			repository.save(commitHistory);
			result++;
		}
		return result;
	}
}
