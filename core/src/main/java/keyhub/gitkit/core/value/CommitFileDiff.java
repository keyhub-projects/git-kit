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
