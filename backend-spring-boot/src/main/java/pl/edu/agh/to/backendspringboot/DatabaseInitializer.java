package pl.edu.agh.to.backendspringboot;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.MedicalFacilities;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Address;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;

import java.time.LocalTime;

public class DatabaseInitializer {

    static void main(String[] args) {
        System.out.println("Starting Database Initialization...");

        ConfigurableApplicationContext context = new SpringApplicationBuilder(BackendSpringBootApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        DoctorRepository doctorRepository = context.getBean(DoctorRepository.class);
        ConsultingRoomRepository consultingRoomRepository = context.getBean(ConsultingRoomRepository.class);
        ScheduleRepository scheduleRepository = context.getBean(ScheduleRepository.class);

        scheduleRepository.deleteAll();
        doctorRepository.deleteAll();
        consultingRoomRepository.deleteAll();

        var doc1 = doctorRepository.save(new Doctor(
                "John", "Doe", "12345678901",
                new Address("Main Street", "Springfield", "12-345"),
                MedicalSpecialization.CARDIOLOGY
        ));

        var doc2 = doctorRepository.save(new Doctor(
                "Jane", "Smith", "98765432109",
                new Address("Second Street", "Shelbyville", "98-765"),
                MedicalSpecialization.CARDIOLOGY
        ));

        var doc3 = doctorRepository.save(new Doctor(
                "Emily", "Johnson", "45678912345",
                new Address("Third Avenue", "Ogdenville", "56-789"),
                MedicalSpecialization.CARDIOLOGY));

        var doc4 = doctorRepository.save(new Doctor(
                "Michael", "Brown", "65432198765",
                new Address("Fourth Boulevard", "North Haverbrook", "65-432"),
                MedicalSpecialization.DERMATOLOGY));

        doctorRepository.save(new Doctor("Sarah", "Davis", "78912345678", new Address("Fifth Lane", "Capital City", "78-912"), MedicalSpecialization.DERMATOLOGY));
        doctorRepository.save(new Doctor("David", "Wilson", "32165498765", new Address("Sixth Road", "Cypress Creek", "32-165"), MedicalSpecialization.ALLERGOLOGY));
        doctorRepository.save(new Doctor("Laura", "Miller", "14725836901", new Address("Seventh Street", "Brockway", "14-725"), MedicalSpecialization.GENERAL_SURGERY));

        var room101 = consultingRoomRepository.save(new ConsultingRoom(
                "101",
                new MedicalFacilities(true, true, true, true, true)
        ));

        var room102 = consultingRoomRepository.save(new ConsultingRoom(
                "102",
                new MedicalFacilities(true, false, true, true, false)
        ));

        var room201 = consultingRoomRepository.save(new ConsultingRoom(
                "201",
                new MedicalFacilities(true, true, false, false, true)
        ));

        scheduleRepository.save(new Schedule(doc1, room101, LocalTime.of(8, 0), LocalTime.of(12, 0)));
        scheduleRepository.save(new Schedule(doc2, room102, LocalTime.of(8, 0), LocalTime.of(12, 0)));
        scheduleRepository.save(new Schedule(doc3, room101, LocalTime.of(13, 0), LocalTime.of(17, 0)));
        scheduleRepository.save(new Schedule(doc4, room201, LocalTime.of(9, 0), LocalTime.of(15, 0)));

        System.out.println("Database initialized successfully");
    }
}