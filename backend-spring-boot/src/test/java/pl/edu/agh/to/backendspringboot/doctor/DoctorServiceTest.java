package pl.edu.agh.to.backendspringboot.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.backendspringboot.application.doctor.DoctorService;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorInfoResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorRequest;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Address;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorInfo;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void shouldAddDoctorSuccessfully() {
        // given
        Address address = new Address("Oak St", "Boston", "32-344");
        DoctorRequest doctorRequest = new DoctorRequest(
                "Jane",
                "Smith",
                "98765432109",
                "INTERNAL_MEDICINE",
                "Oak St",
                "Boston",
                "32-344"
        );
        Doctor doctor = DoctorRequest.toEntity(doctorRequest);


        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        // when
        doctorService.addDoctor(doctorRequest);

        // then
        verify(doctorRepository).save(doctor);
    }

    @Test
    void shouldGetAllDoctorsBrief() {
        // given
        DoctorBrief brief1 = mock(DoctorBrief.class);
        DoctorBrief brief2 = mock(DoctorBrief.class);

        when(doctorRepository.findDoctorsBrief()).thenReturn(List.of(brief1, brief2));

        // when
        List<DoctorBriefResponse> doctors = doctorService.getDoctors();

        // then
        assertThat(doctors).hasSize(2);
        verify(doctorRepository).findDoctorsBrief();
    }

    @Test
    void shouldGetDoctorInfoByIdWhenExists() {
        // given
        Integer doctorId = 1;
        DoctorInfo doctorInfo = mock(DoctorInfo.class);

        when(doctorInfo.getFirstName()).thenReturn("John");

        when(doctorRepository.findDoctorInfoById(doctorId)).thenReturn(Optional.of(doctorInfo));

        // when
        DoctorInfoResponse result = doctorService.getDoctorInfoById(doctorId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("John");
        verify(doctorRepository).findDoctorInfoById(doctorId);
    }

    @Test
    void shouldThrowDoctorNotFoundExceptionWhenDoctorDoesNotExist() {
        // given
        Integer doctorId = 999;
        when(doctorRepository.findDoctorInfoById(doctorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorInfoById(doctorId));
        verify(doctorRepository).findDoctorInfoById(doctorId);
    }

    @Test
    void shouldDeleteDoctorByIdWhenExists() {
        // given
        Integer doctorId = 1;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        doNothing().when(doctorRepository).deleteById(doctorId);

        // when
        doctorService.deleteDoctorById(doctorId);

        // then
        verify(doctorRepository).existsById(doctorId);
        verify(doctorRepository).deleteById(doctorId);
    }

    @Test
    void shouldThrowDoctorNotFoundExceptionWhenDeletingNonExistentDoctor() {
        // given
        Integer doctorId = 999;
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        // when & then
        assertThrows(DoctorNotFoundException.class, () -> doctorService.deleteDoctorById(doctorId));
        verify(doctorRepository).existsById(doctorId);
        verify(doctorRepository, never()).deleteById(anyInt());
    }

}