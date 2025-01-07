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

import keyhub.gitkit.core.annotation.GitOperationAspect;
import keyhub.gitkit.core.value.CommitFileDiff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static keyhub.gitkit.core.annotation.GitOperationAspect.git;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@Slf4j
@EnableAspectJAutoProxy
class SimpleLocalGitTest {

    static String repoPath;
    static LocalGit utd;
    static MockedStatic<GitOperationAspect> mockAspect;

    @BeforeAll
    public static void initTest() {
        utd = LocalGit.init();
        mockAspect = mockStatic(GitOperationAspect.class);
        String workingDir = System.getProperty("user.dir"); // 현재 작업 디렉토리
        if (workingDir.endsWith("core")) {
            repoPath = "../sample";
        } else {
            repoPath = "./sample";
        }
    }

    @Test
    public void 정상_gitRepo_동작() {
        assertNotNull(repoPath);
        assertNotEquals("", repoPath);
    }

    @Test
    public void 정상_Git_동작() throws IOException {
        try (Git git = Git.open(new File(repoPath))) {
            when(git()).thenReturn(git);
            assertNotNull(git());
        }
    }

    @Test
    public void 정상_findCommitByHash_조회() throws IOException {
        try (Git git = Git.open(new File(repoPath))) {
            when(git()).thenReturn(git);
            Optional<RevCommit> oldOption = utd.findCommitByHash("b0f86eb8");
            Optional<RevCommit> newOption = utd.findCommitByHash("c481f436");

            assertNotNull(oldOption.orElse(null));
            assertNotNull(newOption.orElse(null));
        }
    }

    @Test
    public void 정상_findDiffMapperFiles_작동() throws IOException {
        try (Git git = Git.open(new File(repoPath))) {
            when(git()).thenReturn(git);
            Optional<RevCommit> oldOption = utd.findCommitByHash("b0f86eb8");
            Optional<RevCommit> newOption = utd.findCommitByHash("c481f436");
            assertNotNull(oldOption.orElse(null));
            assertNotNull(newOption.orElse(null));

            List<CommitFileDiff> result = utd.findDiffMapperFiles(oldOption.get(), newOption.get());

            assertNotNull(result);
            assertNotEquals(0, result.size());
            log.warn(result.toString());
        }
    }
}