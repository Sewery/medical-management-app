package pl.edu.agh.to.backendspringboot.domain.consulting_room.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConsultingRoomAssignedToScheduleException extends RuntimeException {
    public ConsultingRoomAssignedToScheduleException(String message) {
        super(message);
    }
}