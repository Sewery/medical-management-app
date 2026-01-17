package pl.edu.agh.to.backendspringboot.infrastructure.visit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.MedicalFacilities;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.patient.model.Patient;
import pl.edu.agh.to.backendspringboot.domain.shared.model.Address;
import pl.edu.agh.to.backendspringboot.domain.visit.Visit;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VisitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private VisitRepository visitRepository;

    @Test
    void shouldDetectCollidingVisitForDoctor() {
        Doctor doc = new Doctor("Jan", "L", "1", new Address(), MedicalSpecialization.CARDIOLOGY);
        Patient pat = new Patient("Jan", "P", "2", new Address());
        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true,false,false,false,false));
        entityManager.persist(doc);
        entityManager.persist(pat);
        entityManager.persist(room);
        entityManager.persist(new Visit(pat,doc, LocalTime.of(10, 0), LocalTime.of(10, 30), room));
        entityManager.flush();
        boolean exists = visitRepository.collidingVisitExist(LocalTime.of(10, 0), LocalTime.of(10, 30), doc.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckVisitsForPatient() {
        Doctor doc = new Doctor("Jan", "L", "1", new Address(), MedicalSpecialization.CARDIOLOGY);
        Patient pat = new Patient("Jan", "P", "2", new Address());
        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true,false,false,false,false));
        entityManager.persist(doc);
        entityManager.persist(pat);
        entityManager.persist(room);
        entityManager.persist(new Visit( pat, doc, LocalTime.of(10, 0), LocalTime.of(10, 30),room));
        entityManager.flush();
        assertThat(visitRepository.visitsExistForPatient(pat.getId())).isTrue();
    }

    @Test
    void shouldCheckVisitForSchedule() {
        Doctor doc = new Doctor("Jan", "L", "1", new Address(), MedicalSpecialization.CARDIOLOGY);
        Patient pat = new Patient("Jan", "P", "2", new Address());
        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true,false,false,false,false));
        entityManager.persist(doc);
        entityManager.persist(pat);
        entityManager.persist(room);
        entityManager.persist(new Visit(pat,doc , LocalTime.of(9, 0), LocalTime.of(9, 30),room));
        entityManager.flush();
        assertThat(visitRepository.visitExistsForSchedule(doc.getId(), LocalTime.of(8, 0), LocalTime.of(12, 0))).isTrue();
    }
}