package keyhub.gitkit.core.value;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record CommitFileDiff(
	CommitFile oldFile,
	CommitFile newFile,
	String commitRegisterPersonId,
	String commitComment,
	LocalDateTime commitDatetime,
	String changeType,
	String path
) implements Value {

	public static CommitFileDiff of(DiffEntry diffEntry, RevCommit oldCommit, String oldContents, RevCommit newCommit, String newContents) {
		long datetime = Integer.valueOf(newCommit.getCommitTime()).longValue();
		LocalDateTime commitAt = Instant.ofEpochSecond(datetime)
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
		return new CommitFileDiff(
			new CommitFile(oldCommit.getName(), diffEntry.getOldPath(), oldContents),
			new CommitFile(newCommit.getName(), diffEntry.getNewPath(), newContents),
			newCommit.getCommitterIdent().getName(),
			newCommit.getFullMessage(),
			commitAt,
			diffEntry.getChangeType().name(),
			diffEntry.getNewPath()
		);
	}
}
