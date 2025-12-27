package pl.edu.agh.to.backendspringboot.domain.doctor.model;

public interface DoctorDetail extends DoctorBrief {
    String getPesel();
    Address getAddress();
}
