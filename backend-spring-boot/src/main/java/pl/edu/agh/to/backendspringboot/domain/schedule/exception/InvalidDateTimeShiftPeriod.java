package pl.edu.agh.to.backendspringboot.domain.schedule.exception;

public class InvalidDateTimeShiftPeriod extends RuntimeException {
    public InvalidDateTimeShiftPeriod(String message) {
        super(message);
    }
}
