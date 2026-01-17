package pl.edu.agh.to.backendspringboot.application.consulting_room;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomAssignedToScheduleException;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNumberAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultingRoomServiceTest {

    @Mock
    private ConsultingRoomRepository consultingRoomRepository;
    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ConsultingRoomService consultingRoomService;

    @Test
    void shouldAddRoom() {
        ConsultingRoomRequest req = new ConsultingRoomRequest("101", true, true, false, false, false);
        when(consultingRoomRepository.existsByRoomNumber("101")).thenReturn(false);
        consultingRoomService.addConsultingRoom(req);
        verify(consultingRoomRepository).save(any(ConsultingRoom.class));
    }

    @Test
    void shouldThrowIfRoomNumberExists() {
        ConsultingRoomRequest req = new ConsultingRoomRequest("101", true, true, false, false, false);
        when(consultingRoomRepository.existsByRoomNumber("101")).thenReturn(true);
        assertThrows(ConsultingRoomNumberAlreadyExistsException.class, () -> consultingRoomService.addConsultingRoom(req));
    }

    @Test
    void shouldGetRooms() {
        consultingRoomService.getConsultingRooms();
        verify(consultingRoomRepository).findConsultingRoomsBrief();
    }

    @Test
    void shouldGetRoomDetails() {
        ConsultingRoom room = mock(ConsultingRoom.class);
        when(room.getSchedules()).thenReturn(Collections.emptySet());
        when(consultingRoomRepository.findByIdWithSchedules(1)).thenReturn(Optional.of(room));
        consultingRoomService.getConsultingRoomDetailResponse(1);
    }

    @Test
    void shouldThrowIfRoomNotFound() {
        when(consultingRoomRepository.findByIdWithSchedules(99)).thenReturn(Optional.empty());
        assertThrows(ConsultingRoomNotFoundException.class, () -> consultingRoomService.getConsultingRoomDetailResponse(99));
    }

    @Test
    void shouldDeleteRoom() {
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(scheduleRepository.existsByConsultingRoomId(1)).thenReturn(false);
        consultingRoomService.deleteConsultingRoomById(1);
        verify(consultingRoomRepository).deleteById(1);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentRoom() {
        when(consultingRoomRepository.existsById(99)).thenReturn(false);
        assertThrows(ConsultingRoomNotFoundException.class, () -> consultingRoomService.deleteConsultingRoomById(99));
    }

    @Test
    void shouldThrowWhenDeletingRoomWithSchedule() {
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(scheduleRepository.existsByConsultingRoomId(1)).thenReturn(true);
        assertThrows(ConsultingRoomAssignedToScheduleException.class, () -> consultingRoomService.deleteConsultingRoomById(1));
    }
}