package pl.edu.agh.to.backendspringboot.presentation.schedule.dto;

import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;
import java.time.OffsetDateTime;

public record ScheduleResponse(
        Integer id,
        OffsetDateTime shiftStart,
        OffsetDateTime shiftEnd
) {
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getShiftStart(),
                schedule.getShiftEnd()
        );
    }
}
