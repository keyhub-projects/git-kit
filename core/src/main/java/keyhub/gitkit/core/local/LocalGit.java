package keyhub.gitkit.core.local;

import keyhub.gitkit.core.value.CommitFileDiff;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Optional;

import static keyhub.gitkit.core.annotation.GitOperationAspect.git;

public interface LocalGit {

	static LocalGit init() {
		return SimpleLocalGit.init();
	}

	default Optional<RevCommit> findCommitByHash(String hash){
		return findCommitByHash(git(), hash);
	}
	Optional<RevCommit> findCommitByHash(Git git, String hash);

	default List<DiffEntry> findDiffs(RevCommit oldCommit, RevCommit newCommit){
		return findDiffs(git(), oldCommit, newCommit);
	}
	List<DiffEntry> findDiffs(Git git, RevCommit oldCommit, RevCommit newCommit);

	default List<DiffEntry> findDiffMappers(RevCommit oldCommit, RevCommit newCommit){
		return findDiffMappers(git(), oldCommit, newCommit);
	}
	List<DiffEntry> findDiffMappers(Git git, RevCommit oldCommit, RevCommit newCommit);

	default List<CommitFileDiff> findDiffFiles(RevCommit oldCommit, RevCommit newCommit){
		return findDiffFiles(git(), oldCommit, newCommit);
	}
	List<CommitFileDiff> findDiffFiles(Git git, RevCommit oldCommit, RevCommit newCommit);

	default List<CommitFileDiff> findDiffMapperFiles(RevCommit oldCommit, RevCommit newCommit){
		return findDiffMapperFiles(git(), oldCommit, newCommit);
	}
	List<CommitFileDiff> findDiffMapperFiles(Git git, RevCommit oldCommit, RevCommit newCommit);

	default String readFileContents(ObjectId id){
		return readFileContents(git(), id);
	}
	String readFileContents(Git git, ObjectId id);

	default int stageAll(){
		return stageAll(".");
	}
	default int stageAll(String path){
		return stageAll(git(), path);
	}
	int stageAll(Git git, String path);

	default String commit(String commitMessage){
		return commit(git(), commitMessage);
	}
	String commit(Git git, String commitMessage);

	default String reset(ResetCommand.ResetType mode){
		return reset(git(), mode);
	}
	String reset(Git git, ResetCommand.ResetType mode);

	default String resetHard(){
		return reset(ResetCommand.ResetType.HARD);
	}

	default boolean isConflicted(){
		return isConflicted(git());
	}
	boolean isConflicted(Git git);

	default int resolveConflicts(CheckoutCommand.Stage side){
		return resolveConflicts(git(), side);
	}
	int resolveConflicts(Git git, CheckoutCommand.Stage side);

	default int resolveConflictsWithLocals(){
		return resolveConflicts(CheckoutCommand.Stage.OURS);
	}

	default int resolveConflictsWithOrigins(){
		return resolveConflicts(CheckoutCommand.Stage.THEIRS);
	}
}
