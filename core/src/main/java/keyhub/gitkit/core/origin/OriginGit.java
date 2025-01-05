package keyhub.gitkit.core.origin;

import org.eclipse.jgit.api.Git;

import static keyhub.gitkit.core.annotation.GitOperationAspect.git;

public interface OriginGit {
	static OriginGit of(OriginGitConfigMap configMap){
		return SimpleOriginGit.of(configMap);
	}

	int cloneOrigin();

	default int fetch(){
		return fetch(git());
	}
	int fetch(Git git);

	default int pull(String branchName){
		return pull(git(), branchName);
	}
	int pull(Git git, String branchName);

	default String push(String branchName){
		return push(git(), branchName);
	}
	String push(Git git, String branchName);
}
