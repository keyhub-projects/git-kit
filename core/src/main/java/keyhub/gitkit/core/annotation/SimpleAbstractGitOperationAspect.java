package keyhub.gitkit.core.annotation;

import keyhub.gitkit.core.IllegalGitStateException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.lang.reflect.Field;

@Aspect
public class SimpleAbstractGitOperationAspect extends AbstractGitOperationAspect {
	/**
	 * @throws IllegalGitStateException by IOException | NoSuchFieldException | IllegalAccessException | joinPoint exception
	 */
	@Override
	@Around("@annotation(gitOperation)")
	public Object manageGitResource(ProceedingJoinPoint joinPoint, GitOperation gitOperation) {
		try {

			String repoPath = "";
			String name = gitOperation.repoPath();
			GitOperation.RepoPathFrom repoPathFrom = gitOperation.repoPathFrom();
			if (name == null || name.isBlank()) {
				throw new IllegalGitStateException("No repository path");
			}
			Object target = joinPoint.getTarget();
			switch (repoPathFrom) {
				case FIELD -> {
					Field field = target.getClass().getField(repoPath);
					field.setAccessible(true);
					repoPath = field.get(target).toString();
				}
				case PARAMETER -> {
					MethodSignature signature = (MethodSignature)joinPoint.getSignature();
					var parameterNames = signature.getParameterNames();
					var args = joinPoint.getArgs();
					for (int i = 0; i < parameterNames.length; i++) {
						if (parameterNames[i].equals(name)) {
							repoPath = args[i].toString();
							break;
						}
					}
				}
			}
			try (Git git = Git.open(new File(repoPath))) {
				gitThreadLocal.set(git);
				return joinPoint.proceed();
			} finally {
				gitThreadLocal.remove();
			}
		} catch (Throwable e) {
			throw new IllegalGitStateException(e);
		}
	}
}
