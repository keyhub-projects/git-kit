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

package keyhub.gitkit.starter.service.sync;

import keyhub.gitkit.core.UuidV7Generator;
import keyhub.gitkit.core.annotation.GitOperation;
import keyhub.gitkit.core.local.LocalGit;
import keyhub.gitkit.core.origin.OriginGit;
import keyhub.gitkit.core.origin.OriginGitConfigMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SyncService {
	private final LocalGit localGit;
	private final OriginGit originGit;
	@Value("${git.repo-path}")
	private String gitRepoPath;
	@Value("${git.origin.url}")
	private String originUrl;
	@Value("${git.origin.username}")
	private String originUsername;
	@Value("${git.origin.password}")
	private String originPassword;

	@GitOperation(repoPath = "gitRepoPath", repoPathFrom = GitOperation.RepoPathFrom.FIELD)
	public int syncHard(String branchName) {
		OriginGitConfigMap configMap = new OriginGitConfigMap(originUrl, gitRepoPath, originUsername, originPassword, branchName);
		originGit.fetch(configMap);
		int result = originGit.pull(configMap);
		if(localGit.isConflicted()){
			localGit.resolveConflictsWithLocals();
		}
		return result;
	}

	@GitOperation(repoPath = "gitRepoPath", repoPathFrom = GitOperation.RepoPathFrom.FIELD)
	public String push(String branchName){
		OriginGitConfigMap configMap = new OriginGitConfigMap(originUrl, gitRepoPath, originUsername, originPassword, branchName);
		return originGit.push(configMap);
	}

	@GitOperation(repoPath = "gitRepoPath", repoPathFrom = GitOperation.RepoPathFrom.FIELD)
	public String commit(){
		localGit.stageAll();
		return localGit.commit(UuidV7Generator.generate().toString());
	}
}
