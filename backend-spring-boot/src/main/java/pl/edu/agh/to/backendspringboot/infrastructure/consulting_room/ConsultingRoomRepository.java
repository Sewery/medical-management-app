package pl.edu.agh.to.backendspringboot.infrastructure.consulting_room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoomBrief;

import java.util.List;
import java.util.Optional;

public interface ConsultingRoomRepository extends JpaRepository<ConsultingRoom, Integer> {
    @Query("""
        SELECT d.id AS id,
            d.roomNumber AS roomNumber,
            d.medicalFacilities AS medicalFacilities
            FROM ConsultingRoom d
    """)
    List<ConsultingRoomBrief> findConsultingRoomsBrief();

    @Query("""
        SELECT cr
        FROM ConsultingRoom cr
        LEFT JOIN FETCH cr.schedules s
        LEFT JOIN FETCH s.doctor
        WHERE cr.id = :id
    """)
    Optional<ConsultingRoom> findByIdWithSchedules(@Param("id")Integer id);

    boolean existsByRoomNumber(String roomNumber);

}
