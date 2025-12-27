package pl.edu.agh.to.backendspringboot.infrastructure.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoomBrief;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("""
    SELECT d
    FROM Doctor d
    WHERE NOT EXISTS (
        SELECT s
        FROM Schedule s
        WHERE s.doctor = d
        AND s.shiftStart < :endTime
        AND s.shiftEnd > :startTime
    )
    """)
    List<DoctorBrief> findAvailableDoctorsInPeriod(LocalTime startTime, LocalTime endTime);

    @Query("""
    SELECT cr
    FROM ConsultingRoom cr
    WHERE NOT EXISTS (
        SELECT s
        FROM Schedule s
        WHERE s.consultingRoom = cr
        AND s.shiftStart < :endTime
        AND s.shiftEnd > :startTime
    )
    """)
    List<ConsultingRoomBrief> findAvailableConsultingRoomsInPeriod(LocalTime startTime, LocalTime endTime);

}
