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

package keyhub.gitkit.core.origin;

import keyhub.gitkit.core.annotation.GitOperationAspect;
import keyhub.gitkit.core.local.LocalGit;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;
import java.io.IOException;

import static keyhub.gitkit.core.annotation.GitOperationAspect.git;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@Slf4j
@EnableAspectJAutoProxy
class SimpleOriginGitTest {

    static OriginGit utd;
    static String repoPath;
    static LocalGit localGit;
    static MockedStatic<GitOperationAspect> mockAspect;
    static OriginGitConfigMap config;

    @BeforeEach
    public void initTest() {
        localGit = LocalGit.init();
        mockAspect = mockStatic(GitOperationAspect.class);
        String workingDir = System.getProperty("user.dir"); // 현재 작업 디렉토리
        if (workingDir.endsWith("core")) {
            repoPath = "../sample";
        } else {
            repoPath = "./sample";
        }
        // todo test 위치도 build 아래로 옮겨서 할 것
        // todo 경량 git repo 컨테이너를 띄워 테스트하고,
        //  설정값을 하드코딩 결정적으로 수행해도 되도록 수정할 것
        log.info("username: " + System.getenv("USERNAME"));
        log.info("password: " + System.getenv("PASSWORD"));
        // gradle test 시 환경변수
        config = new OriginGitConfigMap(
                "https://github.com/keyhub-projects/sample.git",
                repoPath,
                System.getenv("USERNAME"),
                System.getenv("PASSWORD"),
                "main"
        );
        utd = SimpleOriginGit.init();
    }

    @Test
    public void 정상_fetch_동작() throws IOException {
        try (Git git = Git.open(new File(repoPath))) {
            when(git()).thenReturn(git);
            int result = utd.fetch(config);
            assertTrue(result > -1);
            log.info("Fetch result: {}", result);
        }
    }

}