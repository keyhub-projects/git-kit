package keyhub.gitkit.core.value;

public record CommitFile(
	String commitHash,
	String filePath,
	String contents
) implements Value {
}
