package keyhub.sample.persistence;

import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

import static keyhub.sample.common.TraceUtil.guid;
import static keyhub.sample.common.TraceUtil.traceUserId;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
	@Embedded
	protected Registration registration;

	@PrePersist
	protected void onCreate() {
		if (getRegistration() == null || getRegisteredDateTime() == null) {
			setRegistered();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		setUpdated();
	}

	protected void setRegistered() {
		this.registration = Registration.from(guid(), traceUserId());
	}

	protected void setUpdated() {
		this.registration.update(guid(), traceUserId());
	}

	public String getGuid() {
		return this.registration.getGuid();
	}

	public String getRegisterPersonId() {
		return this.registration.getRegisterPersonId();
	}

	public LocalDateTime getRegisteredDateTime() {
		return this.registration.getRegisteredDateTime();
	}

	public String getUpdatePersonId() {
		return this.registration.getUpdatePersonId();
	}

	public LocalDateTime getUpdatedDateTime() {
		return this.registration.getUpdatedDateTime();
	}
}
