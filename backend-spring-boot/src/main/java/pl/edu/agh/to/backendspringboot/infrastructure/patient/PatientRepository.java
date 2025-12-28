package pl.edu.agh.to.backendspringboot.infrastructure.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.to.backendspringboot.domain.patient.model.Patient;
import pl.edu.agh.to.backendspringboot.domain.patient.model.PatientBrief;
import pl.edu.agh.to.backendspringboot.domain.patient.model.PatientDetail;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query("select case when count(p) > 0 then true else false end from Patient p where p.pesel = :pesel")
    boolean existsByPesel(String pesel);

    @Query("select p.id as id, p.firstName as firstName, p.lastName as lastName from Patient p")
    List<PatientBrief> findPatientsBrief();

    @Query("select p.id as id, p.firstName as firstName, p.lastName as lastName, p.address as address, p.pesel as pesel from Patient p where p.id = :id")
    Optional<PatientDetail> findPatientInfoById(Integer id);
}