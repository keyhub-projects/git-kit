package keyhub.sample;

import keyhub.sample.dto.*;
import keyhub.sample.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static keyhub.sample.common.TraceUtil.guid;
import static keyhub.sample.common.TraceUtil.traceUserId;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SampleMapperTest {

	@Autowired
	SampleMapper utd;
	@Autowired
	SampleRepository jpaRepository;

	@Nested
	class TransactionManagerTest {
		@Test
		@Transactional
		public void 마이바티스는_Jpa와_트랜잭션매니저를_공유한다() {
			SampleRootAggregateEntity sampleRootAggregateEntity = SampleRootAggregateEntity.from(
				new SampleRootCreateInDto("TransactionManagerTest"));
			traceUserId("tester");
			guid("tester");
			jpaRepository.save(sampleRootAggregateEntity);

			Optional<SampleRootSimpleOutDto> optional = utd.findSimple(sampleRootAggregateEntity.getId());

			assertTrue(optional.isPresent());
			SampleRootSimpleOutDto dto = optional.get();
			assertEquals(sampleRootAggregateEntity.getName(), dto.name());
		}
	}

	@Nested
	class DynamicQueryTest {
		@Test
		@Transactional(readOnly = true)
		public void 마이바티스_VelocityLanguageDriver_작동_테스트() {
			var result = utd.findByVelocityLanguageDriverDoNothing();
			assertNotNull(result);
		}

		@Test
		@Transactional(readOnly = true)
		public void 단일if_테스트() {
			Map<String, String> map = Map.of("name", "string2");
			var result = utd.findByVelocityLanguageDriverOneIf(map);
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional(readOnly = true)
		public void 단일if_테스트2() {
			Map<String, String> map = Map.of();
			var result = utd.findByVelocityLanguageDriverOneIf(map);
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional(readOnly = true)
		public void 다중if_테스트1() {
			Map<String, String> map = Map.of("name1", "1");
			var result = utd.findByVelocityLanguageDriverMultiIf(map);
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional(readOnly = true)
		public void 다중if_테스트2() {
			Map<String, String> map = Map.of("name2", "string2");
			var result = utd.findByVelocityLanguageDriverMultiIf(map);
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional(readOnly = true)
		public void 다중if_테스트3() {
			Map<String, String> map = Map.of("name2", "1");
			var result = utd.findByVelocityLanguageDriverMultiIf(map);
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional(readOnly = true)
		public void repeat_테스트() {
			Map<String, List<String>> map = Map.of("names", List.of("string2", "1"));
			var result = utd.findByVelocityLanguageDriverRepeat(map);
			assertNotNull(result);
			log.warn(result.toString());
		}

		@Test
		@Transactional(readOnly = true)
		public void 문자열_변수_테스트() {
			Map<String, String> map = Map.of();
			var result = utd.findByVelocityLanguageDriverStringVariable();
			assertNotNull(result);
			log.warn(result.toString());
		}


		@Test
		@Transactional(readOnly = true)
		public void 숫자_변수와_산술연산_테스트() {
			Map<String, String> map = Map.of();
			var result = utd.findByVelocityLanguageDriverNumberVariable();
			assertNotNull(result);
			log.warn(result.toString());
		}
	}
}