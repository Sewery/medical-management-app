package pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto;

import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoomBrief;

public record ConsultingRoomBriefResponse(
        Integer id,
        String roomNumber,
        boolean hasExaminationBed,
        boolean hasECGMachine,
        boolean hasScale,
        boolean hasThermometer,
        boolean hasDiagnosticSet
) {
    public static ConsultingRoomBriefResponse from(ConsultingRoomBrief consultingRoomBrief) {
        return new ConsultingRoomBriefResponse(
                consultingRoomBrief.getId(),
                consultingRoomBrief.getRoomNumber(),
                consultingRoomBrief.getMedicalFacilities().isHasExaminationBed(),
                consultingRoomBrief.getMedicalFacilities().isHasECGMachine(),
                consultingRoomBrief.getMedicalFacilities().isHasScale(),
                consultingRoomBrief.getMedicalFacilities().isHasThermometer(),
                consultingRoomBrief.getMedicalFacilities().isHasDiagnosticSet()
        );
    }
}