package pl.edu.agh.to.backendspringboot.domain.doctor.model;

public interface DoctorInfo extends DoctorBrief {
    String getPesel();
    Address getAddress();
}
