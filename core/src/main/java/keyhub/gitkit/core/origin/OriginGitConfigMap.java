package keyhub.gitkit.core.origin;

import lombok.Builder;

@Builder
public record OriginGitConfigMap(
	String remoteUrl,
	String localPath,
	String username,
	String password
) {
}
