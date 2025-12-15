package pl.edu.agh.to.backendspringboot.domain.doctor.model;

public interface DoctorBrief {
    Integer getId();
    String getFirstName();
    String getLastName();
    MedicalSpecialization getSpecialization();
}