package keyhub.sample.dto;

import keyhub.sample.persistence.SampleSubEntity;

import java.time.LocalDateTime;

public record SampleSubSimpleOutDto (
	Long id,
	String name,
	LocalDateTime registeredDateTime,
	LocalDateTime updatedDateTime
) implements OutputDto {
	public static SampleSubSimpleOutDto from(SampleSubEntity member) {
		return new SampleSubSimpleOutDto(member.getId(), member.getName(), member.getRegisteredDateTime(),
			member.getUpdatedDateTime());
	}
}
