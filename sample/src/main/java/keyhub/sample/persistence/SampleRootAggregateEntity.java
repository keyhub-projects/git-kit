package keyhub.sample.persistence;

import jakarta.persistence.*;
import keyhub.sample.dto.SampleRootCreateInDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "SAMPLE_ROOT_AGGREGATE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class SampleRootAggregateEntity extends BaseEntity{
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "NM")
	String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sampleRootAggregateEntity")
	List<SampleSubEntity> sampleSubEntities;

	public static SampleRootAggregateEntity from(SampleRootCreateInDto command) {
		return SampleRootAggregateEntity.builder()
			.name(command.name())
			.sampleSubEntities(new ArrayList<>())
			.build();
	}

	public void putMember(SampleSubEntity member) {
		removeMember(member.getId());
		this.sampleSubEntities.add(member);
	}

	public SampleSubEntity getHomeMember(Long memberId) {
		return this.sampleSubEntities.stream()
			.filter(homeMember -> homeMember.getId().equals(memberId))
			.findFirst()
			.orElse(null);
	}

	public void removeMember(Long memberId) {
		this.sampleSubEntities.removeIf(target->target.getId().equals(memberId));
	}
}

