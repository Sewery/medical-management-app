package pl.edu.agh.to.backendspringboot.doctor.model;

public interface DoctorBrief {
    Integer getId();
    String getFirstName();
    String getLastName();
    MedicalSpecialization getSpecialization();
}