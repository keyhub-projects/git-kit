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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import keyhub.gitkit.core.entity.DiffCommit;
import keyhub.gitkit.core.entity.CommitHistory;
import keyhub.gitkit.core.value.CommitFile;
import keyhub.gitkit.core.value.CommitFileDiff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(fluent = true)
@Getter
@Entity
public class DiffCommitEntity implements DiffCommit<Long> {
    @Id
    Long id;
    String commitContents;
    String recentContents;

    @ManyToOne
    @JoinColumn(name = "commitHistory_id")
    CommitHistoryEntity commitHistory;

    public static DiffCommit<Long> of(
            CommitFileDiff fileDiffDto, CommitHistory<Long> newCommitHistory
    ){
        CommitFile oldFile = fileDiffDto.oldFile();
        CommitFile newFile = fileDiffDto.newFile();
        return DiffCommitEntity.builder()
                .commitContents(newFile.contents())
                .recentContents(oldFile.contents())
                .commitHistory((CommitHistoryEntity) newCommitHistory)
                .build();
    }
}
