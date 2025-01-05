package keyhub.sample.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SampleRepository extends JpaRepository<SampleRootAggregateEntity, Long> {
	default SampleRootAggregateEntity getById(Long id) {
		return findById(id).orElseThrow(()->new IllegalStateException("SampleRootAggregateEntity not found"));
	}

	Optional<SampleRootAggregateEntity> findByName(String name);
}
