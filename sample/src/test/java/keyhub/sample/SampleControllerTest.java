package keyhub.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class SampleControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Nested
	class HandleExceptionTest {
		@Test
		public void controllerAdvice의_Exception_핸들링_테스트() throws Exception {
			var response = mockMvc.perform(
					MockMvcRequestBuilders.get("/sample/test/exception")
				)
				.andExpect(status().is5xxServerError())
				.andReturn()
				.getResponse();
			log.warn(response.getErrorMessage());
		}
	}

}