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
					Field field = target.getClass().getField(name);
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
