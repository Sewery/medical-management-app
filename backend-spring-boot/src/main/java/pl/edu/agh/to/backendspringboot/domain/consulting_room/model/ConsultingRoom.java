package pl.edu.agh.to.backendspringboot.domain.consulting_room.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;

import java.util.Set;

@Entity
@Table(name = ConsultingRoom.TABLE_NAME)
public class ConsultingRoom {

    public static final String TABLE_NAME = "consulting_room";
    @Id
    @GeneratedValue
    private Integer id;
    @NotBlank
    private String roomNumber;

    @Embedded
    @NotNull
    private MedicalFacilities medicalFacilities;

    public ConsultingRoom(String roomNumber, MedicalFacilities medicalFacilities) {
        this.roomNumber = roomNumber;
        this.medicalFacilities = medicalFacilities;
    }

    public ConsultingRoom() {

    }

    public Integer getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public MedicalFacilities getMedicalFacilities() {
        return medicalFacilities;
    }
}
