package keyhub.gitkit.core.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.eclipse.jgit.api.Git;

public interface GitOperationAspect {
	Object manageGitResource(ProceedingJoinPoint joinPoint, GitOperation gitOperation) throws Throwable;

	static Git git() {
		return AbstractGitOperationAspect.git();
	}
}
