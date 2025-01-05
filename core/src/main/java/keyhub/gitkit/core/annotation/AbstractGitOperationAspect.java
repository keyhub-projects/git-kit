package keyhub.gitkit.core.annotation;

import keyhub.gitkit.core.IllegalGitStateException;
import org.eclipse.jgit.api.Git;

public abstract class AbstractGitOperationAspect implements GitOperationAspect {
	protected static ThreadLocal<Git> gitThreadLocal = new ThreadLocal<>();

	protected static Git git() {
		Git git = gitThreadLocal.get();
		if (git == null) {
			throw new IllegalGitStateException("No Git instance in this context.");
		}
		return git;
	}
}
