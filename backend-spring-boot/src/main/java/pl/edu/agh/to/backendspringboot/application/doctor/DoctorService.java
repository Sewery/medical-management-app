package pl.edu.agh.to.backendspringboot.application.doctor;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorInfoResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorRequest;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void addDoctor(DoctorRequest doctorRequest){
        doctorRepository.save(DoctorRequest.toEntity(doctorRequest));
    }

    public List<DoctorBriefResponse> getDoctors() {
        return doctorRepository.findDoctorsBrief().stream().map(DoctorBriefResponse::from).toList();
    }

    public DoctorInfoResponse getDoctorInfoById(Integer id) {
        return doctorRepository.findDoctorInfoById(id).map(DoctorInfoResponse::from)
                .orElseThrow(()->new DoctorNotFoundException("Doctor with id "+id+" not found"));
    }

    public void deleteDoctorById(Integer id) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Doctor with id " + id + " not found");
        }
        doctorRepository.deleteById(id);
    }
}
