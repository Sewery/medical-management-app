package pl.edu.agh.to.backendspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.edu.agh.to.backendspringboot.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.doctor.model.Address;
import pl.edu.agh.to.backendspringboot.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.doctor.model.MedicalSpecialization;

@SpringBootApplication
public class DatabaseInitializer {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(DatabaseInitializer.class, args);


        DoctorRepository doctorRepository = context.getBean(DoctorRepository.class);
        doctorRepository.deleteAll();

        doctorRepository.save(new Doctor(
                "John",
                "Doe",
                "12345678901",
                new Address("Main Street", "Springfield", "12-345"),
                MedicalSpecialization.CARDIOLOGY
        ));

        doctorRepository.save(new Doctor(
                "Jane",
                "Smith",
                "98765432109",
                new Address("Second Street", "Shelbyville", "98-765"),
                MedicalSpecialization.CARDIOLOGY
        ));

        doctorRepository.save(new Doctor(
                "Emily",
                "Johnson",
                "45678912345",
                new Address("Third Avenue", "Ogdenville", "56-789"),
                MedicalSpecialization.CARDIOLOGY));

        doctorRepository.save(new Doctor(
                "Michael",
                "Brown",
                "65432198765",
                new Address("Fourth Boulevard", "North Haverbrook", "65-432"),
                MedicalSpecialization.DERMATOLOGY));

        doctorRepository.save(new Doctor(
                "Sarah",
                "Davis",
                "78912345678",
                new Address("Fifth Lane", "Capital City", "78-912"),
                MedicalSpecialization.DERMATOLOGY));

        doctorRepository.save(new Doctor(
                "David",
                "Wilson",
                "32165498765",
                new Address("Sixth Road", "Cypress Creek", "32-165"),
                MedicalSpecialization.ALLERGOLOGY));

        doctorRepository.save(new Doctor(
                "Laura",
                "Miller",
                "14725836901",
                new Address("Seventh Street", "Brockway", "14-725"),
                MedicalSpecialization.GENERAL_SURGERY));

        System.out.println("Example records added to the database.");
        System.exit(0);
    }
}