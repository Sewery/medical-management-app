package pl.edu.agh.to.backendspringboot;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;

@Component
public class DatabaseClean {

    public static void main(String[] args) {

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

        System.out.println("Database cleaned.");
        System.exit(0);
    }
}