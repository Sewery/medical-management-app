package pl.edu.agh.to.backendspringboot.infrastructure.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorDetail;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Query("select d.id as id, d.firstName as firstName, d.lastName as lastName, d.specialization as specialization from Doctor d")
    List<DoctorBrief> findDoctorsBrief();

    @Query("select d.id as id, d.firstName as firstName, d.lastName as lastName, d.specialization as specialization, d.address as address, d.pesel as pesel from Doctor d where d.id = :id")
    Optional<DoctorDetail> findDoctorInfoById(Integer id);

    @Query("select d from Doctor d where d.specialization = :specialization")
    List<DoctorBrief> findAllBySpecialization(MedicalSpecialization specialization);
}