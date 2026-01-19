package pl.edu.agh.to.backendspringboot.application.shared;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.infrastructure.configuration.AppProperties;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class DateValidator {

    private final AppProperties properties;
    private final Clock clock;

    public DateValidator(AppProperties properties) {
        this.properties = properties;
        this.clock = Clock.systemDefaultZone();
    }

    public void validateDateRange(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now(clock);
        int maxDays = properties.getMaxDaysInAdvance();

        if (startTime.isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot schedule in the past.");
        }

        if (startTime.isAfter(now.plusDays(maxDays))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Cannot schedule more than %d days in advance.", maxDays));
        }
    }

    public int getMaxDaysInAdvance() {
        return properties.getMaxDaysInAdvance();
    }
}