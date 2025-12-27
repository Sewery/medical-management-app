package pl.edu.agh.to.backendspringboot.domain.consulting_room.exception;

public class ConsultingRoomNotFoundException extends RuntimeException {
    public ConsultingRoomNotFoundException(String message) {
        super(message);
    }
}
