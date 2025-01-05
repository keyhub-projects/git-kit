package keyhub.sample.dto;

import keyhub.sample.persistence.SampleRootAggregateEntity;
import keyhub.sample.persistence.SampleSubEntity;

import java.util.List;

public record SampleRootDetailOutDto (
	SampleRootSimpleOutDto home,
	List<SampleSubSimpleOutDto> members
) implements OutputDto {

	public static SampleRootDetailOutDto of(
			SampleRootAggregateEntity sampleRootAggregateEntity,
			List<SampleSubEntity> members
	) {
		return new SampleRootDetailOutDto(
			SampleRootSimpleOutDto.from(sampleRootAggregateEntity),
			members.stream().map(SampleSubSimpleOutDto::from).toList()
		);
	}
}
