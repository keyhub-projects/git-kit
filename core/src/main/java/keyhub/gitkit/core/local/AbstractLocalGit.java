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

public abstract class AbstractLocalGit implements LocalGit{

    public Optional<RevCommit> findCommitByHash(String hash){
        return findCommitByHash(git(), hash);
    }
    abstract Optional<RevCommit> findCommitByHash(Git git, String hash);

    public List<DiffEntry> findDiffs(RevCommit oldCommit, RevCommit newCommit){
        return findDiffs(git(), oldCommit, newCommit);
    }
    abstract List<DiffEntry> findDiffs(Git git, RevCommit oldCommit, RevCommit newCommit);

    public List<DiffEntry> findDiffMappers(RevCommit oldCommit, RevCommit newCommit){
        return findDiffMappers(git(), oldCommit, newCommit);
    }
    abstract List<DiffEntry> findDiffMappers(Git git, RevCommit oldCommit, RevCommit newCommit);

    public List<CommitFileDiff> findDiffFiles(RevCommit oldCommit, RevCommit newCommit){
        return findDiffFiles(git(), oldCommit, newCommit);
    }
    abstract List<CommitFileDiff> findDiffFiles(Git git, RevCommit oldCommit, RevCommit newCommit);

    public List<CommitFileDiff> findDiffMapperFiles(RevCommit oldCommit, RevCommit newCommit){
        return findDiffMapperFiles(git(), oldCommit, newCommit);
    }
    abstract List<CommitFileDiff> findDiffMapperFiles(Git git, RevCommit oldCommit, RevCommit newCommit);

    public String readFileContents(ObjectId id){
        return readFileContents(git(), id);
    }
    abstract String readFileContents(Git git, ObjectId id);

    public int stageAll(){
        return stageAll(".");
    }
    public int stageAll(String path){
        return stageAll(git(), path);
    }
    abstract int stageAll(Git git, String path);

    public String commit(String commitMessage){
        return commit(git(), commitMessage);
    }
    abstract String commit(Git git, String commitMessage);

    public String reset(ResetCommand.ResetType mode){
        return reset(git(), mode);
    }
    abstract String reset(Git git, ResetCommand.ResetType mode);

    public String resetHard(){
        return reset(ResetCommand.ResetType.HARD);
    }

    public boolean isConflicted(){
        return isConflicted(git());
    }
    abstract boolean isConflicted(Git git);

    public int resolveConflicts(CheckoutCommand.Stage side){
        return resolveConflicts(git(), side);
    }
    abstract int resolveConflicts(Git git, CheckoutCommand.Stage side);
    public int resolveConflictsWithLocals(){
        return resolveConflicts(CheckoutCommand.Stage.OURS);
    }
    public int resolveConflictsWithOrigins(){
        return resolveConflicts(CheckoutCommand.Stage.THEIRS);
    }
}
