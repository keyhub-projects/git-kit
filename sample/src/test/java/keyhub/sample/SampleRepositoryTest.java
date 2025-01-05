package keyhub.sample;

import keyhub.sample.dto.SampleRootCreateInDto;
import keyhub.sample.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static keyhub.sample.common.TraceUtil.traceUserId;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class SampleRepositoryTest {

	@Autowired
	SampleRepository utd;

	@Nested
	class RepositoryHealthTest {
		@Test
		@Transactional
		public void repository가_정상동작하는지() {
			var result = utd.findAll();
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional
		public void repository가_정상save하는지() {
			traceUserId("tester");
			SampleRootCreateInDto dto = new SampleRootCreateInDto("test");
			SampleRootAggregateEntity entity = SampleRootAggregateEntity.from(dto);
			var result = utd.save(entity);
			assertNotNull(result);
			log.warn(result.toString());
		}
	}
}