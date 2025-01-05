package keyhub.sample.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Registration {
	@Column(name = "GLOB_UID")
	String guid;

	@Column(name = "REGP_ID")
	String registerPersonId;
	@Column(name = "REG_DTM")
	LocalDateTime registeredDateTime;

	@Column(name = "UPDP_ID")
	String updatePersonId;
	@Column(name = "UPD_DTM")
	LocalDateTime updatedDateTime;

	public static Registration from(String guid, String registerPersonId) {
		LocalDateTime dateTime = LocalDateTime.now();
		return Registration.builder()
			.guid(guid)
			.registerPersonId(registerPersonId)
			.registeredDateTime(dateTime)
			.updatePersonId(registerPersonId)
			.updatedDateTime(dateTime)
			.build();
	}

	public void update(String guid, String updatePersonId) {
		this.guid = guid;
		this.updatePersonId = updatePersonId;
		this.updatedDateTime = LocalDateTime.now();
	}
}
