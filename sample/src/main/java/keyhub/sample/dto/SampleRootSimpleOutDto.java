package keyhub.sample.dto;

import keyhub.sample.persistence.SampleRootAggregateEntity;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SampleRootSimpleOutDto (
	Long id,
	String name,
	LocalDateTime registeredDateTime,
	LocalDateTime updatedDateTime
) implements OutputDto {

	public static SampleRootSimpleOutDto from(SampleRootAggregateEntity sampleRootAggregateEntity) {
		return SampleRootSimpleOutDto.builder()
			.id(sampleRootAggregateEntity.getId())
			.name(sampleRootAggregateEntity.getName())
			.registeredDateTime(sampleRootAggregateEntity.getRegistration().getRegisteredDateTime())
			.updatedDateTime(sampleRootAggregateEntity.getRegistration().getUpdatedDateTime())
			.build();
	}
}
