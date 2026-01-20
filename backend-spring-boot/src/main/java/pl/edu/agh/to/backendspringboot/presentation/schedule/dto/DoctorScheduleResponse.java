package pl.edu.agh.to.backendspringboot.presentation.schedule.dto;

import pl.edu.agh.to.backendspringboot.domain.schedule.model.ScheduleBrief;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomBriefResponse;

public record DoctorScheduleResponse(
        Integer id,
        ConsultingRoomBriefResponse consultingRoom,
        ScheduleResponse dutyTime
) {
    public static DoctorScheduleResponse from(ScheduleBrief schedule) {
        return new DoctorScheduleResponse(
                schedule.getId(),
                ConsultingRoomBriefResponse.from(schedule.getConsultingRoom()),
                ScheduleResponse.from(schedule)
        );
    }
}