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

package keyhub.gitkit.starter.entity;

import jakarta.persistence.*;
import keyhub.gitkit.core.entity.DiffCommit;
import keyhub.gitkit.core.entity.CommitHistory;
import keyhub.gitkit.core.value.CommitFileDiff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(fluent = true)
@Getter
@Entity
public class CommitHistoryEntity implements CommitHistory<Long> {

    @Id
    Long id;
    String commitPoint;
    String comparingCommitPoint;
    String commitRegisterPersonId;
    LocalDateTime commitRegisteredDateTime;
    String commitComment;
    String approveYn;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "commitHistory")
    List<DiffCommitEntity> diffCommits;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "commitHistory")
    List<DiffQueryCommitEntity> diffQueryCommits;

    public static CommitHistory<Long> of(CommitFileDiff fileDiffDto) {
        return CommitHistoryEntity.builder()
                .commitPoint(fileDiffDto.newFile().commitHash())
                .comparingCommitPoint(fileDiffDto.oldFile().commitHash())
                .commitRegisterPersonId(fileDiffDto.commitRegisterPersonId())
                .commitRegisteredDateTime(fileDiffDto.commitDatetime())
                .commitComment(fileDiffDto.commitComment())
                .approveYn("N")
                .build();
    }

    @Override
    public <DID, T extends DiffCommit<DID>> void addAll(List<T> diffs) {
        for (T diff : diffs) {
            add(diff);
        }
    }

    @Override
    public <DID, T extends DiffCommit<DID>> void add(T diff) {
        switch (diff) {
            case DiffCommitEntity commitDiff -> diffCommits.add(commitDiff);
            case DiffQueryCommitEntity commitQueryDiff -> diffQueryCommits.add(commitQueryDiff);
            default -> throw new IllegalStateException("Unexpected value: " + diff);
        }
    }
}
