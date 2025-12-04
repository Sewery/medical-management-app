package pl.edu.agh.to.backendspringboot.doctor;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
}
