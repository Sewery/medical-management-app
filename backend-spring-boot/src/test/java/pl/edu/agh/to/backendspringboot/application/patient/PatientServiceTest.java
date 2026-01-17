package pl.edu.agh.to.backendspringboot.application.patient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.patient.model.Patient;
import pl.edu.agh.to.backendspringboot.domain.patient.model.PatientBrief;
import pl.edu.agh.to.backendspringboot.domain.shared.model.Address;
import pl.edu.agh.to.backendspringboot.domain.visit.VisitBriefPatient;
import pl.edu.agh.to.backendspringboot.infrastructure.patient.PatientRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.visit.VisitRepository;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldAddPatientSuccessfully() {
        // given
        PatientRequest request = new PatientRequest("Jan", "Kowalski", "90010112345", "Krakowska 1", "Kraków", "30-001");
        when(patientRepository.existsByPesel(request.pesel())).thenReturn(false);

        // when
        patientService.addPatient(request);

        // then
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicatePatient() {
        // given
        PatientRequest request = new PatientRequest("Jan", "Kowalski", "90010112345", "Krakowska 1", "Kraków", "30-001");
        when(patientRepository.existsByPesel(request.pesel())).thenReturn(true);

        // when & then
        assertThrows(PatientAlreadyExistsException.class, () -> patientService.addPatient(request));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldGetAllPatients() {
        // given
        Patient patient = mock(Patient.class);
        when(patient.getId()).thenReturn(1);
        when(patient.getFirstName()).thenReturn("Jan");
        when(patient.getLastName()).thenReturn("Kowalski");

        when(patientRepository.findAll()).thenReturn(List.of(patient));

        // when
        List<PatientBriefResponse> result = patientService.getPatients();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("Jan");
    }

    @Test
    void shouldGetPatientInfoById() {
        // given
        int patientId = 1;
        Patient patient = mock(Patient.class);
        Address address = mock(Address.class);

        when(patient.getId()).thenReturn(patientId);
        when(patient.getAddress()).thenReturn(address);
        when(address.getStreet()).thenReturn("Krakowska 1");
        when(address.getCity()).thenReturn("Kraków");
        when(address.getPostalCode()).thenReturn("30-001");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(visitRepository.findAllByPatientId(patientId)).thenReturn(Collections.emptyList());

        // when
        PatientDetailResponse response = patientService.getPatientInfoById(patientId);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        // given
        int patientId = 99;
        when(visitRepository.findAllByPatientId(patientId)).thenReturn(Collections.emptyList());
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientInfoById(patientId));
    }

    @Test
    void shouldDeletePatientSuccessfully() {
        // given
        int patientId = 1;
        when(patientRepository.existsById(patientId)).thenReturn(true);
        when(visitRepository.visitsExistForPatient(patientId)).thenReturn(false);

        // when
        patientService.deletePatientById(patientId);

        // then
        verify(patientRepository).deleteById(patientId);
    }

    @Test
    void shouldThrowWhenDeletingPatientWithVisits() {
        // given
        int patientId = 1;
        when(patientRepository.existsById(patientId)).thenReturn(true);
        when(visitRepository.visitsExistForPatient(patientId)).thenReturn(true);

        // when & then
        assertThrows(PatientAlreadyExistsException.class, () -> patientService.deletePatientById(patientId));
        verify(patientRepository, never()).deleteById(anyInt());
    }
}