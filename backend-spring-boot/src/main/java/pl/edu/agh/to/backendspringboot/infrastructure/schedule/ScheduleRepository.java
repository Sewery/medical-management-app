package pl.edu.agh.to.backendspringboot.infrastructure.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;

public interface ScheduleRepository  extends JpaRepository<Schedule, Integer> {
}
