package pl.edu.agh.to.backendspringboot.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void addDoctorSuccessfully() {
        // given
        Doctor doctor = new Doctor(/* valid data */);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        // when
        doctorService.addDoctor(doctor);

        // then
        verify(doctorRepository).save(doctor);
    }

}