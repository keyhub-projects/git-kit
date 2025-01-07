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
import keyhub.gitkit.core.StringQueryParser;
import keyhub.gitkit.core.entity.CommitHistory;
import keyhub.gitkit.core.entity.DiffQueryCommit;
import keyhub.gitkit.core.value.CommitFile;
import keyhub.gitkit.core.value.CommitFileDiff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(fluent = true)
@Getter
@Entity
public class DiffQueryCommitEntity implements DiffQueryCommit<Long> {
    @Id
    Long id;

    String recentContents;
    String commitContents;

    String queryPath;
    String approveYn;
    String changeType;
    
    @ManyToOne
    @JoinColumn(name = "commitHistory_id")
    CommitHistoryEntity commitHistory;

    public static List<DiffQueryCommitEntity> ofAll(
            CommitFileDiff fileDiffDto, CommitHistory<Long> newCommitHistory
    ) {
        CommitFile oldFile = fileDiffDto.oldFile();
        Map<String, String> oldQueryMap = StringQueryParser.extractQueries(oldFile.contents());
        CommitFile newFile = fileDiffDto.newFile();
        Map<String, String> newQueryMap = StringQueryParser.extractQueries(newFile.contents());
        List<DiffQueryCommitEntity> queryDiffs = new ArrayList<>();
        // 새로운 쿼리와 이전 쿼리 비교
        for (String methodName : newQueryMap.keySet()) {
            String newQuery = newQueryMap.get(methodName);
            String oldQuery = oldQueryMap.get(methodName);
            if (oldQuery == null || oldQuery.isEmpty()) {
                oldQuery = "";
            }
            if (!newQuery.equals(oldQuery)) {
                queryDiffs.add(DiffQueryCommitEntity.of(fileDiffDto, (CommitHistoryEntity) newCommitHistory, methodName, newQuery, oldQuery));
            }
        }
        // 삭제된 메서드
        for (String methodName : oldQueryMap.keySet()) {
            if (!newQueryMap.containsKey(methodName)) {
                queryDiffs.add(
                        DiffQueryCommitEntity.of(fileDiffDto, (CommitHistoryEntity) newCommitHistory, methodName, "", oldQueryMap.get(methodName)));
            }
        }
        return queryDiffs;
    }

    public static DiffQueryCommitEntity of(
            CommitFileDiff fileDiffDto, CommitHistory<Long> commitHistory,
            String methodName, String content, String recentContent
    ) {
        return of(fileDiffDto, (CommitHistoryEntity) commitHistory, methodName, content, recentContent);
    }

    public static DiffQueryCommitEntity of(
            CommitFileDiff fileDiffDto,
            CommitHistoryEntity commitHistory,
            String methodName, String content, String recentContent
    ) {
        String filePath = fileDiffDto.path();
        filePath = filePath.substring(filePath.indexOf("kr/co/milkt/"))
                .replace(".java", "")
                .replaceAll("/", ".");
        return DiffQueryCommitEntity.builder()
                .commitHistory(commitHistory)
                .queryPath(filePath + "::" + methodName)
                .commitContents(content)
                .recentContents(recentContent)
                .approveYn("N")
                .changeType(fileDiffDto.changeType())
                .build();
    }
}
