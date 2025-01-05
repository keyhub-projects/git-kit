package keyhub.gitkit.core.origin;

import keyhub.gitkit.core.annotation.GitOperationAspect;
import keyhub.gitkit.core.local.LocalGit;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    public static void initTest() {
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
        OriginGitConfigMap config = new OriginGitConfigMap(
                "https://github.com/keyhub-projects/sample.git",
                repoPath,
                System.getenv("USERNAME"),
                System.getenv("PASSWORD")
        );
        utd = SimpleOriginGit.of(config);
    }

    @Test
    public void 정상_fetch_동작() throws IOException {
        try (Git git = Git.open(new File(repoPath))) {
            when(git()).thenReturn(git);
            int result = utd.fetch(git());
            assertTrue(result > -1);
            log.info("Fetch result: {}", result);
        }
    }

}