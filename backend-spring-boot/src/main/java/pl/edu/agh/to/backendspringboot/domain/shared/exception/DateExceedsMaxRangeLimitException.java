package pl.edu.agh.to.backendspringboot.domain.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DateExceedsMaxRangeLimitException extends RuntimeException {

    public DateExceedsMaxRangeLimitException(String message) {
        super(message);
    }

    public DateExceedsMaxRangeLimitException(int maxDays) {
        super(String.format("Cannot schedule more than %d days in advance.", maxDays));
    }
}