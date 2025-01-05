package keyhub.sample.service;

import keyhub.sample.dto.*;
import keyhub.sample.persistence.SampleMapper;
import keyhub.sample.persistence.SampleRepository;
import keyhub.sample.persistence.SampleRootAggregateEntity;
import keyhub.sample.persistence.SampleSubEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SampleService {
	private final SampleRepository repository;
	private final SampleMapper mapper;

	@Transactional(readOnly = true)
	public List<SampleRootSimpleOutDto> all() {
		return mapper.findAllSimple();
	}

	@Transactional(readOnly = true)
	public SampleRootDetailOutDto detail(Long homeId) {
		SampleRootAggregateEntity sampleRootAggregateEntity = repository.getById(homeId);
		return SampleRootDetailOutDto.of(sampleRootAggregateEntity, sampleRootAggregateEntity.getSampleSubEntities());
	}

	@Transactional
	public Long create(SampleRootCreateInDto dto) {
		SampleRootAggregateEntity sampleRootAggregateEntity = SampleRootAggregateEntity.from(dto);
		repository.save(sampleRootAggregateEntity);
		return sampleRootAggregateEntity.getId();
	}

	@Transactional
	public Long update(Long id, SampleRootUpdateInDto dto) {
		SampleRootAggregateEntity sampleRootAggregateEntity = repository.getById(id);
		sampleRootAggregateEntity.setName(dto.name());
		repository.save(sampleRootAggregateEntity);
		return sampleRootAggregateEntity.getId();
	}

	@Transactional
	public Long delete(Long id) {
		SampleRootAggregateEntity sampleRootAggregateEntity = repository.getById(id);
		repository.delete(sampleRootAggregateEntity);
		return sampleRootAggregateEntity.getId();
	}

	@Transactional
	public Long addMember(Long homeId, SampleSubAddInDto dto) {
		SampleRootAggregateEntity sampleRootAggregateEntity = repository.getById(homeId);
		SampleSubEntity member = SampleSubEntity.from(dto);
		sampleRootAggregateEntity.putMember(member);
		repository.save(sampleRootAggregateEntity);
		return member.getId();
	}

	@Transactional
	public Long updateMember(Long homeId, Long memberId, SampleSubUpdateInDto dto) {
		SampleRootAggregateEntity sampleRootAggregateEntity = repository.getById(homeId);
		SampleSubEntity member = sampleRootAggregateEntity.getHomeMember(memberId);
		member.setName(dto.name());
		sampleRootAggregateEntity.putMember(member);
		repository.save(sampleRootAggregateEntity);
		return memberId;
	}

	@Transactional
	public Long deleteMember(Long homeId, Long memberId) {
		SampleRootAggregateEntity sampleRootAggregateEntity = repository.getById(homeId);
		sampleRootAggregateEntity.removeMember(memberId);
		repository.save(sampleRootAggregateEntity);
		return sampleRootAggregateEntity.getId();
	}
}
