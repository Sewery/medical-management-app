package pl.edu.agh.to.backendspringboot.application.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoomBrief;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.MedicalFacilities;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.ConflictInScheduleTimePeriod;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.InvalidScheduleTimePeriod;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.VisitAssignedToScheduleException;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.visit.VisitRepository;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.AvailabilityResponse;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.ScheduleRequest;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ConsultingRoomRepository consultingRoomRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void shouldThrowExceptionWhenEndTimeBeforeStartTime() {
        // given
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(9, 0);

        // when & then
        assertThrows(InvalidScheduleTimePeriod.class,
                () -> scheduleService.getAvailableDoctorsAndConsultingRooms(start, end));
    }

    @Test
    void shouldThrowExceptionWhenPeriodIsTooShort() {
        // given
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(10, 29);

        // when & then
        assertThrows(InvalidScheduleTimePeriod.class,
                () -> scheduleService.getAvailableDoctorsAndConsultingRooms(start, end));
    }

    @Test
    void shouldThrowExceptionWhenPeriodIsTooLong() {
        // given
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 1); // 12h 1min

        // when & then
        assertThrows(InvalidScheduleTimePeriod.class,
                () -> scheduleService.getAvailableDoctorsAndConsultingRooms(start, end));
    }

    @Test
    void shouldReturnAvailableResourcesWhenTimeIsValid() {
        // given
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(10, 0);

        DoctorBrief doctorMock = mock(DoctorBrief.class);
        when(doctorMock.getId()).thenReturn(1);
        when(doctorMock.getFirstName()).thenReturn("Jan");
        when(doctorMock.getLastName()).thenReturn("Kowalski");
        when(doctorMock.getSpecialization()).thenReturn(MedicalSpecialization.CARDIOLOGY);

        ConsultingRoomBrief roomMock = mock(ConsultingRoomBrief.class);
        MedicalFacilities facilitiesMock = mock(MedicalFacilities.class); // Unikamy NPE w mapperze
        when(roomMock.getId()).thenReturn(1);
        when(roomMock.getRoomNumber()).thenReturn("101");
        when(roomMock.getMedicalFacilities()).thenReturn(facilitiesMock);

        when(scheduleRepository.findAvailableDoctorsInPeriod(start, end)).thenReturn(List.of(doctorMock));
        when(scheduleRepository.findAvailableConsultingRoomsInPeriod(start, end)).thenReturn(List.of(roomMock));

        // when
        AvailabilityResponse response = scheduleService.getAvailableDoctorsAndConsultingRooms(start, end);

        // then
        assertNotNull(response);
        assertEquals(1, response.doctors().size());
        assertEquals("Jan", response.doctors().get(0).firstName());
        assertEquals(1, response.consultingRooms().size());
        assertEquals("101", response.consultingRooms().get(0).roomNumber());
    }

    @Test
    void shouldAddScheduleWhenNoConflicts() {
        // given
        ScheduleRequest request = new ScheduleRequest(
                LocalTime.of(8, 0), LocalTime.of(9, 0), 1, 10
        );

        Doctor doctorMock = mock(Doctor.class);
        ConsultingRoom roomMock = mock(ConsultingRoom.class);

        // Znaleziono lekarza i gabinet
        when(doctorRepository.findById(1)).thenReturn(Optional.of(doctorMock));
        when(consultingRoomRepository.findById(10)).thenReturn(Optional.of(roomMock));

        // Brak konfliktów
        when(scheduleRepository.existsScheduleInPeriodForConsultingDoctor(any(), any(), eq(10))).thenReturn(false);
        when(scheduleRepository.existsScheduleInPeriodForDoctor(any(), any(), eq(1))).thenReturn(false);

        // when
        scheduleService.addSchedule(request);

        // then
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void shouldThrowException_WhenDoctorNotFound() {
        // given
        ScheduleRequest request = new ScheduleRequest(
                LocalTime.of(8, 0), LocalTime.of(9, 0), 99, 10
        );
        when(doctorRepository.findById(99)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DoctorNotFoundException.class, () -> scheduleService.addSchedule(request));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenRoomNotFound() {
        // given
        ScheduleRequest request = new ScheduleRequest(
                LocalTime.of(8, 0), LocalTime.of(9, 0), 1, 99
        );
        when(doctorRepository.findById(1)).thenReturn(Optional.of(mock(Doctor.class)));
        when(consultingRoomRepository.findById(99)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ConsultingRoomNotFoundException.class, () -> scheduleService.addSchedule(request));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenRoomConflictExists() {
        // given
        ScheduleRequest request = new ScheduleRequest(
                LocalTime.of(8, 0), LocalTime.of(9, 0), 1, 10
        );

        when(doctorRepository.findById(1)).thenReturn(Optional.of(mock(Doctor.class)));
        when(consultingRoomRepository.findById(10)).thenReturn(Optional.of(mock(ConsultingRoom.class)));

        // Konflikt w gabinecie
        when(scheduleRepository.existsScheduleInPeriodForConsultingDoctor(
                request.startTime(), request.endTime(), request.consultingRoomId())
        ).thenReturn(true);

        // when & then
        assertThrows(ConflictInScheduleTimePeriod.class, () -> scheduleService.addSchedule(request));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDoctorConflictExists() {
        // given
        ScheduleRequest request = new ScheduleRequest(
                LocalTime.of(8, 0), LocalTime.of(9, 0), 1, 10
        );

        when(doctorRepository.findById(1)).thenReturn(Optional.of(mock(Doctor.class)));
        when(consultingRoomRepository.findById(10)).thenReturn(Optional.of(mock(ConsultingRoom.class)));

        // Konflikt - doktor w tym czasie ma dyżur
        when(scheduleRepository.existsScheduleInPeriodForConsultingDoctor(any(), any(), anyInt())).thenReturn(false);
        when(scheduleRepository.existsScheduleInPeriodForDoctor(
                request.startTime(), request.endTime(), request.doctorId())
        ).thenReturn(true);

        // when & then
        assertThrows(ConflictInScheduleTimePeriod.class, () -> scheduleService.addSchedule(request));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void shouldDeleteScheduleById() {
        // given
        int scheduleId = 1;
        Schedule schedule = mock(Schedule.class);
        Doctor doctor = mock(Doctor.class);

        when(scheduleRepository.existsById(scheduleId)).thenReturn(true);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(schedule.getDoctor()).thenReturn(doctor);
        when(doctor.getId()).thenReturn(10);
        when(schedule.getShiftStart()).thenReturn(LocalTime.of(8, 0));
        when(schedule.getShiftEnd()).thenReturn(LocalTime.of(12, 0));

        when(visitRepository.visitExistsForSchedule(10, LocalTime.of(8,0), LocalTime.of(12,0))).thenReturn(false);

        // when
        scheduleService.deleteScheduleById(scheduleId);

        // then
        verify(scheduleRepository).deleteById(scheduleId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentSchedule() {
        // given
        int scheduleId = 99;
        when(scheduleRepository.existsById(scheduleId)).thenReturn(false);

        // when & then
        assertThrows(pl.edu.agh.to.backendspringboot.domain.schedule.exception.ScheduleNotFoundException.class,
                () -> scheduleService.deleteScheduleById(scheduleId));
    }

    @Test
    void shouldThrowExceptionWhenVisitsExistForSchedule() {
        // given
        int scheduleId = 1;
        Schedule schedule = mock(Schedule.class);
        Doctor doctor = mock(Doctor.class);

        when(scheduleRepository.existsById(scheduleId)).thenReturn(true);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(schedule.getDoctor()).thenReturn(doctor);
        when(doctor.getId()).thenReturn(10);
        when(schedule.getShiftStart()).thenReturn(LocalTime.of(8, 0));
        when(schedule.getShiftEnd()).thenReturn(LocalTime.of(12, 0));

        when(visitRepository.visitExistsForSchedule(10, LocalTime.of(8,0), LocalTime.of(12,0))).thenReturn(true);

        // when & then
        assertThrows(VisitAssignedToScheduleException.class,
                () -> scheduleService.deleteScheduleById(scheduleId));
    }


}