package pl.edu.agh.to.backendspringboot.application.visit;

import io.reactivex.rxjava3.observers.TestObserver;
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
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.patient.model.Patient;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.ScheduleDetail;
import pl.edu.agh.to.backendspringboot.domain.visit.Visit;
import pl.edu.agh.to.backendspringboot.domain.visit.VisitDetail;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitNotInScheduleException;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.patient.PatientRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.visit.VisitRepository;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.AvailabilityResponse;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.VisitRequest;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private ConsultingRoomRepository consultingRoomRepository;

    @InjectMocks
    private VisitService visitService;

    // --- Testy CRUD ---

    @Test
    void shouldDeleteVisitById() {
        when(visitRepository.existsById(1)).thenReturn(true);
        visitService.deleteVisitById(1);
        verify(visitRepository).deleteById(1);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentVisit() {
        when(visitRepository.existsById(99)).thenReturn(false);
        assertThrows(VisitNotFoundException.class, () -> visitService.deleteVisitById(99));
    }

    @Test
    void shouldGetAllVisits() {
        visitService.getAllVisits();
        verify(visitRepository).findAllVisits();
    }

    @Test
    void shouldGetVisitById() {
        VisitDetail mockDetail = mock(VisitDetail.class);
        when(visitRepository.findById(1)).thenReturn(mockDetail);
        visitService.getVisitById(1);
        verify(visitRepository).findById(1);
    }

    // --- Testy addVisit (Walidacja) ---

    @Test
    void shouldThrowWhenDoctorNotFound() {
        VisitRequest req = new VisitRequest(99, 1, 1, LocalTime.now(), LocalTime.now().plusMinutes(30));
        when(doctorRepository.existsById(99)).thenReturn(false);
        assertThrows(DoctorNotFoundException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldThrowWhenPatientNotFound() {
        VisitRequest req = new VisitRequest(1, 99, 1, LocalTime.now(), LocalTime.now().plusMinutes(30));
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(99)).thenReturn(false);
        assertThrows(PatientNotFoundException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldThrowWhenRoomNotFound() {
        VisitRequest req = new VisitRequest(1, 1, 99, LocalTime.now(), LocalTime.now().plusMinutes(30));
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(consultingRoomRepository.existsById(99)).thenReturn(false);
        assertThrows(ConsultingRoomNotFoundException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldThrowWhenVisitExistsForDoctor() {
        VisitRequest req = new VisitRequest(1, 1, 1, LocalTime.of(10,0), LocalTime.of(10,30));
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(visitRepository.visitAlreadyExistsForDoctor(anyInt(), any(), any())).thenReturn(true);

        assertThrows(VisitAlreadyExistsException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldThrowWhenVisitExistsForRoom() {
        VisitRequest req = new VisitRequest(1, 1, 1, LocalTime.of(10,0), LocalTime.of(10,30));
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(visitRepository.visitAlreadyExistsForDoctor(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForConsultingRoom(anyInt(), any(), any())).thenReturn(true);

        assertThrows(VisitAlreadyExistsException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldThrowWhenVisitExistsForPatient() {
        VisitRequest req = new VisitRequest(1, 1, 1, LocalTime.of(10,0), LocalTime.of(10,30));
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(visitRepository.visitAlreadyExistsForDoctor(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForConsultingRoom(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForPatient(anyInt(), any(), any())).thenReturn(true);

        assertThrows(VisitAlreadyExistsException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldThrowWhenNoScheduleForDoctor() {
        VisitRequest req = new VisitRequest(1, 1, 1, LocalTime.of(10,0), LocalTime.of(10,30));
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(visitRepository.visitAlreadyExistsForDoctor(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForConsultingRoom(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForPatient(anyInt(), any(), any())).thenReturn(false);
        when(scheduleRepository.ScheduleExistsForDoctorInPeriodInRoom(anyInt(), anyInt(), any(), any())).thenReturn(false);

        assertThrows(VisitNotInScheduleException.class, () -> visitService.addVisit(req));
    }

    @Test
    void shouldAddVisitSuccessfully() {
        VisitRequest req = new VisitRequest(1, 1, 1, LocalTime.of(10,0), LocalTime.of(10,30));


        when(doctorRepository.existsById(1)).thenReturn(true);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(consultingRoomRepository.existsById(1)).thenReturn(true);
        when(visitRepository.visitAlreadyExistsForDoctor(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForConsultingRoom(anyInt(), any(), any())).thenReturn(false);
        when(visitRepository.visitAlreadyExistsForPatient(anyInt(), any(), any())).thenReturn(false);


        when(scheduleRepository.ScheduleExistsForDoctorInPeriodInRoom(anyInt(), anyInt(), any(), any())).thenReturn(true);

        when(doctorRepository.findById(1)).thenReturn(Optional.of(mock(Doctor.class)));
        when(patientRepository.findById(1)).thenReturn(Optional.of(mock(Patient.class)));
        when(consultingRoomRepository.findById(1)).thenReturn(Optional.of(mock(ConsultingRoom.class)));

        visitService.addVisit(req);

        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void shouldReturnPossibleVisits() {
        // given
        MedicalSpecialization spec = MedicalSpecialization.CARDIOLOGY; // Visit time e.g., 30 min

        DoctorBrief docBrief = mock(DoctorBrief.class);
        when(docBrief.getId()).thenReturn(1);
        when(doctorRepository.findAllBySpecialization(spec)).thenReturn(List.of(docBrief));

        Doctor doc = mock(Doctor.class);
        when(doc.getId()).thenReturn(1);
        when(doc.getFirstName()).thenReturn("Jan");
        when(doc.getLastName()).thenReturn("Kowalski");
        when(doc.getSpecialization()).thenReturn(spec);

        ConsultingRoomBrief roomBrief = mock(ConsultingRoomBrief.class);
        when(roomBrief.getId()).thenReturn(10);
        when(roomBrief.getRoomNumber()).thenReturn("101");

        MedicalFacilities facilities = mock(MedicalFacilities.class);
        when(facilities.isHasExaminationBed()).thenReturn(true);
        when(roomBrief.getMedicalFacilities()).thenReturn(facilities);

        ScheduleDetail schedule = mock(ScheduleDetail.class);
        when(schedule.getDoctor()).thenReturn(doc);
        when(schedule.getConsultingRoom()).thenReturn(roomBrief);
        when(schedule.getShiftStart()).thenReturn(LocalTime.of(8, 0));
        when(schedule.getShiftEnd()).thenReturn(LocalTime.of(9, 0));

        when(scheduleRepository.findAllByDoctorIdDetail(1)).thenReturn(List.of(schedule));

        // Ensure no colliding visits
        when(visitRepository.collidingVisitExist(any(), any(), anyInt())).thenReturn(false);

        // when
        TestObserver<AvailabilityResponse> observer = visitService.getPossibleVisits(spec).test();

        // then
        observer.awaitCount(2);
        observer.assertNoErrors();
        observer.assertValueCount(2);
    }
}