package pl.edu.agh.to.backendspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.edu.agh.to.backendspringboot.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.doctor.model.Address;
import pl.edu.agh.to.backendspringboot.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.doctor.model.MedicalSpecialization;

@SpringBootApplication
public class DatabaseClean {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(DatabaseInitializer.class, args);


        DoctorRepository doctorRepository = context.getBean(DoctorRepository.class);
        doctorRepository.deleteAll();

        System.out.println("Database cleaned.");
        System.exit(0);
    }
}