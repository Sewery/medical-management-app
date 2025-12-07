package pl.edu.agh.to.backendspringboot.doctor;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.backendspringboot.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.doctor.model.DoctorBrief;
import pl.edu.agh.to.backendspringboot.doctor.model.DoctorInfo;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void addDoctor(Doctor doctor){
        doctorRepository.save(doctor);
    }


    public List<DoctorBrief> getDoctors() {
        return doctorRepository.findDoctorsBrief();
    }

    public DoctorInfo getDoctorInfoById(Integer id) {
        return doctorRepository.findDoctorInfoById(id).orElseThrow(()->new DoctorNotFoundException("Doctor with id "+id+" not found"));
    }

    public boolean deleteDoctorById(Integer id) {
        if(doctorRepository.existsById(id)){
            doctorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
