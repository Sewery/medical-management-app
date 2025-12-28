package pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto;

import jakarta.validation.constraints.NotBlank;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.MedicalFacilities;

public record ConsultingRoomRequest(
        @NotBlank(message = "Room number is mandatory") String roomNumber,
        boolean hasExaminationBed, // Defaults to false if missing in JSON
        boolean hasECGMachine,
        boolean hasScale,
        boolean hasThermometer,
        boolean hasDiagnosticSet
) {
    public ConsultingRoom toEntity() {
        return new ConsultingRoom(
                this.roomNumber,
                new MedicalFacilities(
                        this.hasExaminationBed,
                        this.hasECGMachine,
                        this.hasScale,
                        this.hasThermometer,
                        this.hasDiagnosticSet
                )
        );
    }
}
