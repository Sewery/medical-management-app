package pl.edu.agh.to.backendspringboot.infrastructure.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoomBrief;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.ScheduleBrief;

import java.time.LocalTime;
import java.time.OffsetTime;
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


    @Query("""
        SELECT CASE WHEN COUNT(s)>0 THEN TRUE ELSE FALSE END
        FROM Schedule s
        WHERE s.doctor.id = :doctorId
        AND s.shiftStart < :endTime
        AND s.shiftEnd > :startTime
    """)
    boolean existsScheduleInPeriodForDoctor(LocalTime startTime, LocalTime endTime,int doctorId);

    @Query("""
        SELECT CASE WHEN COUNT(s)>0 THEN TRUE ELSE FALSE END
        FROM Schedule s
        WHERE s.consultingRoom.id = :consultingRoomId
        AND s.shiftStart < :endTime
        AND s.shiftEnd > :startTime
    """)
    boolean existsScheduleInPeriodForConsultingDoctor(LocalTime startTime, LocalTime endTime,int consultingRoomId);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId")
    List<ScheduleBrief> findAllByDoctorId(@Param("doctorId") Integer doctorId);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM Schedule s
        WHERE s.doctor.id = :doctorId
    """)
    boolean existsByDoctorId(@Param("doctorId") Integer doctorId);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM Schedule s
        WHERE s.consultingRoom.id = :consultingRoomId
    """)
    boolean existsByConsultingRoomId(@Param("consultingRoomId") Integer consultingRoomId);
}
