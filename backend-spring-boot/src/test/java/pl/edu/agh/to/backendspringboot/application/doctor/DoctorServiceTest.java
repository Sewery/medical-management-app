package pl.edu.agh.to.backendspringboot.application.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorAssignedToScheduleException;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorDetail;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.shared.model.Address;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.visit.VisitRepository;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorRequest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private VisitRepository visitRepository; // Dodano mocka

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void shouldAddDoctor() {
        DoctorRequest req = new DoctorRequest("J", "K", "123", "CARDIOLOGY", "A", "C", "00");
        doctorService.addDoctor(req);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldGetDoctors() {
        doctorService.getDoctors();
        verify(doctorRepository).findDoctorsBrief();
    }

    @Test
    void shouldReturnDoctorDetailByIdWhenExists() {
        // given
        Integer doctorId = 1;
        DoctorDetail doctorDetail = mock(DoctorDetail.class);
        Address address = mock(Address.class);

        when(doctorDetail.getFirstName()).thenReturn("John");
        when(doctorDetail.getSpecialization()).thenReturn(MedicalSpecialization.CARDIOLOGY); // Mock specialization
        when(doctorDetail.getAddress()).thenReturn(address); // Mock address
        when(address.getPostalCode()).thenReturn("00-123"); // Mock postal code

        when(doctorRepository.findDoctorInfoById(doctorId)).thenReturn(Optional.of(doctorDetail));

        // when
        DoctorDetailResponse result = doctorService.getDoctorInfoById(doctorId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.specialization()).isEqualTo("Kardiologia");
        verify(doctorRepository).findDoctorInfoById(doctorId);
    }

    @Test
    void shouldThrowIfDoctorNotFound() {
        when(doctorRepository.findDoctorInfoById(99)).thenReturn(Optional.empty());
        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorInfoById(99));
    }

    @Test
    void shouldDeleteDoctor() {
        int id = 1;
        when(doctorRepository.existsById(id)).thenReturn(true);
        when(scheduleRepository.existsByDoctorId(id)).thenReturn(false);
        doctorService.deleteDoctorById(id);
        verify(doctorRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentDoctor() {
        when(doctorRepository.existsById(99)).thenReturn(false);
        assertThrows(DoctorNotFoundException.class, () -> doctorService.deleteDoctorById(99));
    }

    @Test
    void shouldThrowWhenDeletingDoctorWithSchedule() {
        int id = 1;
        when(doctorRepository.existsById(id)).thenReturn(true);
        when(scheduleRepository.existsByDoctorId(id)).thenReturn(true);
        assertThrows(DoctorAssignedToScheduleException.class, () -> doctorService.deleteDoctorById(id));
    }
}