package keyhub.sample.controller;

import keyhub.sample.common.BusinessException;
import keyhub.sample.dto.*;
import keyhub.sample.service.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/sample")
@RestController
public class SampleController {
	private final SampleService service;

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	@GetMapping("/all")
	public List<SampleRootSimpleOutDto> all() {
		return service.all();
	}

//	@GetMapping("/list")
//	public List<SampleRootSimpleOutDto> list(
//		@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
//		@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
//	) {
//		return null;
//	}

	@GetMapping("/detail/{id}")
	public SampleRootDetailOutDto detail(@PathVariable("id") Long id) {
		return service.detail(id);
	}

	@PostMapping("/new")
	public Long create(@RequestBody SampleRootCreateInDto dto) {
		return service.create(dto);
	}

	@PostMapping("/{id}")
	public Long update(@PathVariable("id") Long id, @RequestBody SampleRootUpdateInDto dto) {
		return service.update(id, dto);
	}

	@DeleteMapping("/{id}")
	public Long delete(@PathVariable("id") Long id) {
		return service.delete(id);
	}

	@PostMapping("/{id}/new")
	public Long addMember(@PathVariable("id") Long id, @RequestBody SampleSubAddInDto dto) {
		return service.addMember(id, dto);
	}

	@PostMapping("/{id}/{subId}")
	public Long updateMember(@PathVariable("id") Long id, @PathVariable("subId") Long subId,
		@RequestBody SampleSubUpdateInDto dto) {
		return service.updateMember(id, subId, dto);
	}

	@DeleteMapping("/{id}/{subId}")
	public Long deleteMember(@PathVariable("id") Long id, @PathVariable("subId") Long subId) {
		return service.deleteMember(id, subId);
	}

	@GetMapping("/test/exception")
	public Throwable testException() {
		throw new BusinessException("test exception");
	}
}
