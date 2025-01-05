package keyhub.sample.persistence;

import jakarta.persistence.*;
import keyhub.sample.dto.SampleSubAddInDto;
import lombok.*;

@Table(name = "SAMPLE_SUB")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class SampleSubEntity extends BaseEntity{
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "NM")
	String name;

	@ManyToOne
	@JoinColumn(name = "ROOT_ID")
	SampleRootAggregateEntity sampleRootAggregateEntity;

	public static SampleSubEntity from(SampleSubAddInDto command) {
		return SampleSubEntity.builder()
			.name(command.name())
			.build();
	}
}
