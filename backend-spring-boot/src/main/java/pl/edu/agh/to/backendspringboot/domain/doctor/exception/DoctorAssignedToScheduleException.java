package pl.edu.agh.to.backendspringboot.domain.doctor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DoctorAssignedToScheduleException extends RuntimeException {
    public DoctorAssignedToScheduleException(String message) {
        super(message);
    }
}