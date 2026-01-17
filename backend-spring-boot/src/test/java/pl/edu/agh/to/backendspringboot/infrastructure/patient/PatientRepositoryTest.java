package pl.edu.agh.to.backendspringboot.infrastructure.patient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.edu.agh.to.backendspringboot.domain.patient.model.Patient;
import pl.edu.agh.to.backendspringboot.domain.patient.model.PatientBrief;
import pl.edu.agh.to.backendspringboot.domain.patient.model.PatientDetail;
import pl.edu.agh.to.backendspringboot.domain.shared.model.Address;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldCheckIfPatientExistsByPesel() {
        // given
        Patient patient = new Patient("Jan", "Kowalski", "99010100000", new Address("Ulica", "Miasto", "00-000"));
        entityManager.persist(patient);

        // when
        boolean exists = patientRepository.existsByPesel("99010100000");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindPatientsBrief() {
        // given
        Patient p1 = new Patient("Jan", "Test", "11111111111", new Address());
        Patient p2 = new Patient("Anna", "Test", "22222222222", new Address());
        entityManager.persist(p1);
        entityManager.persist(p2);

        // when
        List<PatientBrief> result = patientRepository.findPatientsBrief();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(PatientBrief::getPesel).contains("11111111111", "22222222222");
    }

    @Test
    void shouldFindPatientInfoById() {
        // given
        Address addr = new Address("Polna", "Waw", "00-111");
        Patient p = new Patient("Tomasz", "Kot", "33333333333", addr);
        entityManager.persist(p);

        // when
        Optional<PatientDetail> result = patientRepository.findPatientInfoById(p.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getAddress().getCity()).isEqualTo("Waw");
    }
}