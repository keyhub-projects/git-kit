package keyhub.gitkit.core;

public class IllegalGitStateException extends RuntimeException{
	public IllegalGitStateException() {
		super();
	}

	public IllegalGitStateException(String s) {
		super(s);
	}

	public IllegalGitStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalGitStateException(Throwable cause) {
		super(cause);
	}
}
