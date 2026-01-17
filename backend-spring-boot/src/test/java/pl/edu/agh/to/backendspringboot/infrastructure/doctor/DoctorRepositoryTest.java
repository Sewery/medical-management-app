package pl.edu.agh.to.backendspringboot.infrastructure.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.*;
import pl.edu.agh.to.backendspringboot.domain.shared.model.Address;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindDoctorsBriefProjection() {
        // given
        Address address = new Address("Main St", "City", "00-001");
        Doctor doctor = new Doctor("John", "Doe", "12345678901", address, MedicalSpecialization.CARDIOLOGY);
        entityManager.persist(doctor);
        entityManager.flush();

        // when
        List<DoctorBrief> result = doctorRepository.findDoctorsBrief();

        // then
        assertThat(result).hasSize(1);
        DoctorBrief brief = result.get(0);
        assertThat(brief.getFirstName()).isEqualTo("John");
        assertThat(brief.getLastName()).isEqualTo("Doe");
        assertThat(brief.getSpecialization()).isEqualTo(MedicalSpecialization.CARDIOLOGY);
    }

    @Test
    void shouldFindDoctorInfoById() {
        // given
        Address addr = new Address("Main St", "NY", "00-000");
        Doctor doctor = new Doctor("Alice", "Smith", "98765432109", addr, MedicalSpecialization.PEDIATRICS);
        entityManager.persist(doctor);
        entityManager.flush();

        // when
        Optional<DoctorDetail> result = doctorRepository.findDoctorInfoById(doctor.getId());

        // then
        assertThat(result).isPresent();
        DoctorDetail detail = result.get();
        assertThat(detail.getFirstName()).isEqualTo("Alice");
        assertThat(detail.getAddress().getCity()).isEqualTo("NY");
        assertThat(detail.getPesel()).isEqualTo("98765432109");
    }

    @Test
    void shouldReturnEmptyWhenDoctorInfoNotFound() {
        // when
        Optional<DoctorDetail> result = doctorRepository.findDoctorInfoById(999);
        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllBySpecialization() {
        // given
        Address addr = new Address("Street", "City", "00-000");
        Doctor d1 = new Doctor("Jan", "Kowalski", "111", addr, MedicalSpecialization.CARDIOLOGY);
        Doctor d2 = new Doctor("Anna", "Nowak", "222", addr, MedicalSpecialization.DERMATOLOGY);
        Doctor d3 = new Doctor("Piotr", "Zieliński", "333", addr, MedicalSpecialization.CARDIOLOGY);

        entityManager.persist(d1);
        entityManager.persist(d2);
        entityManager.persist(d3);
        entityManager.flush();

        // when
        List<DoctorBrief> cardiologists = doctorRepository.findAllBySpecialization(MedicalSpecialization.CARDIOLOGY);

        // then
        assertThat(cardiologists).hasSize(2);
        assertThat(cardiologists).extracting(DoctorBrief::getLastName)
                .containsExactlyInAnyOrder("Kowalski", "Zieliński");
    }

    @Test
    void shouldReturnEmptyListWhenNoDoctorsWithSpecialization() {
        // given
        Address addr = new Address("Street", "City", "00-000");
        Doctor d1 = new Doctor("Jan", "Kowalski", "111", addr, MedicalSpecialization.DERMATOLOGY);
        entityManager.persist(d1);
        entityManager.flush();

        // when
        List<DoctorBrief> result = doctorRepository.findAllBySpecialization(MedicalSpecialization.UROLOGY);

        // then
        assertThat(result).isEmpty();
    }
}