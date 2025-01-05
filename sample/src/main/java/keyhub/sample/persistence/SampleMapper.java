package keyhub.sample.persistence;

import keyhub.sample.dto.SampleRootSimpleOutDto;
import org.apache.ibatis.annotations.*;
import org.mybatis.scripting.velocity.VelocityLanguageDriver;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface SampleMapper {

	@Select("""
			select ID as id, NM as name, REG_DTM as registeredDateTime, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
		""")
	List<SampleRootSimpleOutDto> findAllSimple();

	@Select("""
  			select ID as id, NM as name, REG_DTM as registeredDateTime, UPD_DTM as updatedDateTime
  			from SAMPLE_ROOT_AGGREGATE where ID = #{id}
		""")
	Optional<SampleRootSimpleOutDto> findSimple(Long id);

	@Insert("""
			insert SAMPLE_ROOT_AGGREGATE(NM, REGP_ID, REG_DTM)
			values ('test', 'tester', GETDATE())
		""")
	int insertMustBeFail();

	@Update("""
			update SAMPLE_ROOT_AGGREGATE
			set NM = #{command.name}
				, UPDP_ID = 'test'
		    	, UPD_DTM = GETDATE()
			where id = 1
		""")
	int updateMustBeFail();

	@Lang(VelocityLanguageDriver.class)
	@Select("""
			select
				ID as id
				, NM as name
				, REG_DTM as registeredDateTime
				, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
			where 1 = 1
		""")
	List<SampleRootSimpleOutDto> findByVelocityLanguageDriverDoNothing();

	@Lang(VelocityLanguageDriver.class)
	@Select("""
			select
				ID as id
				, NM as name
				, REG_DTM as registeredDateTime
				, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
			#if($_parameter.name)
			where NM = @{name}
			#end
		""")
	List<SampleRootSimpleOutDto> findByVelocityLanguageDriverOneIf(Map<String, String> map4Test);

	@Lang(VelocityLanguageDriver.class)
	@Select("""
			select
				ID as id
				, NM as name
				, REG_DTM as registeredDateTime
				, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
			#where()
				#if($_parameter.name1)
					and NM = @{name1}
				#elseif($_parameter.name2 && $_parameter.name2.length() > 1)
					and NM = @{name2}
				#else
					and NM = '1'
				#end
			#end
		""")
	List<SampleRootSimpleOutDto> findByVelocityLanguageDriverMultiIf(Map<String, String> map4Test);

	@Lang(VelocityLanguageDriver.class)
	@Select("""
			select
				ID as id
				, NM as name
				, REG_DTM as registeredDateTime
				, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
			#where()
				#repeat($_parameter.names $name ',' 'NM IN (' ')')
					@{name}
				#end
			#end
		""")
	List<SampleRootSimpleOutDto> findByVelocityLanguageDriverRepeat(Map<String, List<String>> map4Test);

	@Lang(VelocityLanguageDriver.class)
	@Select("""
			#set($foo = 'string')
			#set($bar = "2")
			#set($fooBar = $foo + $bar)
			select
				ID as id
				, NM as name
				, REG_DTM as registeredDateTime
				, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
			#where()
				NM = @{fooBar}
			#end
		""")
	List<SampleRootSimpleOutDto> findByVelocityLanguageDriverStringVariable();

	@Lang(VelocityLanguageDriver.class)
	@Select("""
			#set($foo = 2)
			#set($bar = $foo + 3)
			#set($fooBar = $foo + $bar)
			select
				ID as id
				, NM as name
				, REG_DTM as registeredDateTime
				, UPD_DTM as updatedDateTime
			from SAMPLE_ROOT_AGGREGATE
			#where()
				ID = @{fooBar}
			#end
		""")
	List<SampleRootSimpleOutDto> findByVelocityLanguageDriverNumberVariable();

}
