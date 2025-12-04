package pl.edu.agh.to.backendspringboot.doctor;

public class InvalidMedicalSpecialization extends RuntimeException {
    public InvalidMedicalSpecialization(String message) {
        super(message);
    }
}
