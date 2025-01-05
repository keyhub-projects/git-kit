package keyhub.sample.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j(topic = "core.handler.exception")
@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
public class DefaultControllerAdvice {
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handleException(BusinessException exception,
		HttpServletRequest request) {
		log.error("BizException - " + exception.getMessage());
		return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SystemException.class)
	public ResponseEntity<?> handleException(SystemException exception,
	 	HttpServletRequest request) {
		log.error("SysException - " + exception.getMessage());
		return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<?> handleException(Throwable exception,
		HttpServletRequest request) {
		log.error("Exception - " + exception.getMessage());
		return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

