package pl.edu.agh.to.backendspringboot.infrastructure.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoomBrief;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.MedicalFacilities;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;
import pl.edu.agh.to.backendspringboot.domain.shared.model.Address;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScheduleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScheduleRepository scheduleRepository;


    @Test
    void shouldReturnAvailableDoctorsInPeriod() {
        // given
        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true, false, false, false, false));
        entityManager.persist(room);

        Address address = new Address("Długa 5", "Kraków", "30-000");

        Doctor busyDoctor = new Doctor(
                "Jan",
                "Zajety",
                "12345678901",
                address,
                MedicalSpecialization.CARDIOLOGY
        );
        entityManager.persist(busyDoctor);

        Schedule schedule = new Schedule(busyDoctor, room, LocalTime.of(10, 0), LocalTime.of(12, 0));
        entityManager.persist(schedule);

        Doctor freeDoctor = new Doctor(
                "Anna",
                "Wolna",
                "98765432109",
                new Address("Krótka 1", "Warszawa", "00-100"),
                MedicalSpecialization.DERMATOLOGY
        );
        entityManager.persist(freeDoctor);

        entityManager.flush();

        // when
        List<DoctorBrief> result = scheduleRepository.findAvailableDoctorsInPeriod(
                LocalTime.of(11, 0), LocalTime.of(13, 0)
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Wolna");
    }


    @Test
    void shouldReturnAvailableConsultingRoomsInPeriod() {
        // given
        Doctor doctor = new Doctor(
                "Test",
                "Doctor",
                "11122233344",
                new Address("Testowa", "Testowo", "11-111"),
                MedicalSpecialization.GENERAL_SURGERY
        );
        entityManager.persist(doctor);

        ConsultingRoom busyRoom = new ConsultingRoom("101", new MedicalFacilities(true, false, false, false, false));
        entityManager.persist(busyRoom);
        entityManager.persist(new Schedule(doctor, busyRoom, LocalTime.of(8, 0), LocalTime.of(9, 0)));

        ConsultingRoom freeRoom = new ConsultingRoom("202", new MedicalFacilities(false, false, false, false, false));
        entityManager.persist(freeRoom);

        entityManager.flush();

        // when
        List<ConsultingRoomBrief> result = scheduleRepository.findAvailableConsultingRoomsInPeriod(
                LocalTime.of(8, 30), LocalTime.of(9, 30)
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoomNumber()).isEqualTo("202");
    }


    @Test
    void shouldReturnTrueWhenDoctorHasOverlap() {
        // given
        Doctor doctor = new Doctor(
                "Jan",
                "Kowalski",
                "55566677788",
                new Address("Szpitalna", "Poznań", "60-100"),
                MedicalSpecialization.UROLOGY
        );
        entityManager.persist(doctor);

        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true, false, false, false, false));
        entityManager.persist(room);

        entityManager.persist(new Schedule(doctor, room, LocalTime.of(12, 0), LocalTime.of(14, 0)));
        entityManager.flush();

        // when & then
        boolean overlap1 = scheduleRepository.existsScheduleInPeriodForDoctor(
                LocalTime.of(12, 30), LocalTime.of(13, 30), doctor.getId());
        assertThat(overlap1).isTrue();


        boolean overlap2 = scheduleRepository.existsScheduleInPeriodForDoctor(
                LocalTime.of(13, 0), LocalTime.of(15, 0), doctor.getId());
        assertThat(overlap2).isTrue();
    }

    @Test
    void shouldReturnFalseWhenDoctorIsFreeTouchingEdges() {
        // given
        Doctor doctor = new Doctor(
                "Jan",
                "Nowak",
                "99988877766",
                new Address("Morska", "Gdynia", "81-000"),
                MedicalSpecialization.UROLOGY
        );
        entityManager.persist(doctor);

        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true, false, false, false, false));
        entityManager.persist(room);

        // Dyżur 12:00 - 14:00
        entityManager.persist(new Schedule(doctor, room, LocalTime.of(12, 0), LocalTime.of(14, 0)));
        entityManager.flush();

        // when
        // Pytamy o 14:00 - 15:00 (stykają się, ale nie nakładają)
        boolean exists = scheduleRepository.existsScheduleInPeriodForDoctor(
                LocalTime.of(14, 0), LocalTime.of(15, 0), doctor.getId());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenRoomIsOccupied() {
        // given
        Doctor doctor = new Doctor(
                "Piotr",
                "Zieliński",
                "12312312312",
                new Address("Piłkarska", "Warszawa", "00-001"),
                MedicalSpecialization.UROLOGY
        );
        entityManager.persist(doctor);

        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true, false, false, false, false));
        entityManager.persist(room);

        // Dyżur w tym gabinecie 10:00 - 11:00
        entityManager.persist(new Schedule(doctor, room, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        entityManager.flush();

        // when
        boolean exists = scheduleRepository.existsScheduleInPeriodForConsultingDoctor(
                LocalTime.of(10, 30), LocalTime.of(11, 30), room.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckScheduleExistsForDoctorInPeriodInRoom() {
        Doctor doc = new Doctor("Jan", "L", "1", new Address(), MedicalSpecialization.CARDIOLOGY);
        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true,false,false,false,false));
        entityManager.persist(doc);
        entityManager.persist(room);
        entityManager.persist(new Schedule(doc, room, LocalTime.of(8,0), LocalTime.of(12,0)));
        entityManager.flush();
        boolean result = scheduleRepository.ScheduleExistsForDoctorInPeriodInRoom(doc.getId(), room.getId(), LocalTime.of(9,0), LocalTime.of(9,30));
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenScheduleExistsByDoctorId() {
        Doctor doc = new Doctor("Jan", "L", "1", new Address(), MedicalSpecialization.CARDIOLOGY);
        ConsultingRoom room = new ConsultingRoom("101", new MedicalFacilities(true,false,false,false,false));
        entityManager.persist(doc);
        entityManager.persist(room);
        entityManager.persist(new Schedule(doc, room, LocalTime.of(8,0), LocalTime.of(12,0)));
        entityManager.flush();

        assertThat(scheduleRepository.existsByDoctorId(doc.getId())).isTrue();
    }
}