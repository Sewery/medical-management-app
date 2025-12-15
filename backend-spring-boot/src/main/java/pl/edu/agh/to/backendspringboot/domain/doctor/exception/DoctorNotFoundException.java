package pl.edu.agh.to.backendspringboot.domain.doctor.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
}
