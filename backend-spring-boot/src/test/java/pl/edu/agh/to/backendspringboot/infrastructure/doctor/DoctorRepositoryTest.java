package pl.edu.agh.to.backendspringboot.infrastructure.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void shouldFindDoctorsBriefProjection() {
        // given
        Doctor doctor = new Doctor("John", "Doe", "123", new Address(), MedicalSpecialization.CARDIOLOGY);
        doctorRepository.save(doctor);

        // when
        List<DoctorBrief> result = doctorRepository.findDoctorsBrief();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("John");
        assertThat(result.getFirst().getSpecialization()).isEqualTo(MedicalSpecialization.CARDIOLOGY);
    }

    @Test
    void shouldFindDoctorInfoProjection() {
        // given
        Address addr = new Address("Main St", "NY", "00-000");
        Doctor doctor = new Doctor("Alice", "Smith", "999", addr, MedicalSpecialization.NONE);
        doctor = doctorRepository.save(doctor); // Save returns the entity with generated ID

        // when
        Optional<DoctorDetail> result = doctorRepository.findDoctorInfoById(doctor.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getAddress().getCity()).isEqualTo("NY");
    }
}