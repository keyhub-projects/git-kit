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

package keyhub.gitkit.core.origin;

import keyhub.gitkit.core.IllegalGitStateException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;

import java.io.File;

public class SimpleOriginGit extends AbstractOriginGit {

	private SimpleOriginGit() {
	}

	protected static SimpleOriginGit init() {
		return new SimpleOriginGit();
	}

	@Override
	public int cloneOrigin(OriginGitConfigMap config) {
		try {
			Git git = Git.cloneRepository()
				.setURI(config.remoteUrl())
				.setDirectory(new File(config.localPath()))
				.setCredentialsProvider(
						new UsernamePasswordCredentialsProvider(config.username(), config.password())
				)
				.call();
			if (git != null) {
				try (git) {
					return 1;
				}
			}
			return 0;
		} catch (GitAPIException e) {
			throw new IllegalGitStateException(e);
		}
	}

	@Override
	public int fetch(Git git, OriginGitConfigMap config) {
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(config.username(), config.password());
		try {
			FetchResult result = git.fetch()
				.setCredentialsProvider(credentialsProvider)
				.call();
			return result.getTrackingRefUpdates().size();
		} catch (GitAPIException e) {
			throw new IllegalGitStateException("Failed to fetch", e);
		}
	}

	@Override
	public int pull(Git git, OriginGitConfigMap config) {
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(config.username(), config.password());
		try {
			PullResult result = git.pull()
				.setCredentialsProvider(credentialsProvider)
				.setRemoteBranchName(config.branchName())
				.call();
			if (result.getMergeResult() == null || result.getMergeResult().getMergedCommits() == null) {
				return 0;
			} else {
				return result.getMergeResult().getMergedCommits().length;
			}
		} catch (GitAPIException e) {
			throw new IllegalGitStateException("Failed to pull", e);
		}
	}

	@Override
	public String push(Git git, OriginGitConfigMap config){
		Iterable<PushResult> results;
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(config.username(), config.password());
		try {
			results =  git.push()
				.setCredentialsProvider(credentialsProvider)
				.call();
		} catch (GitAPIException e) {
			throw new IllegalGitStateException("Failed to pull", e);
		}
		StringBuilder sb = new StringBuilder();
		for(PushResult result : results){
			result.getRemoteUpdates().forEach(update -> {
				sb.append("Ref: %s -> Status: %s"
					.formatted(update.getRemoteName(), update.getStatus()));
				if (update.getStatus().equals(RemoteRefUpdate.Status.REJECTED_OTHER_REASON)
					|| update.getStatus().equals(RemoteRefUpdate.Status.REJECTED_NONFASTFORWARD)
				) {
					sb.append(" | Reason: ")
						.append(update.getMessage());
					throw new IllegalGitStateException(sb.toString());
				}
			});
		}
		return sb.toString();
	}
}
