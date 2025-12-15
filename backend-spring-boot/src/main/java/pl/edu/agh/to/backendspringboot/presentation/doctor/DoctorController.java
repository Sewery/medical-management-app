package pl.edu.agh.to.backendspringboot.presentation.doctor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.application.doctor.DoctorService;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorInfoResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorRequest;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.InvalidMedicalSpecialization;

import java.util.List;

@RestController
@RequestMapping("doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public void addDoctor(@Valid @RequestBody DoctorRequest doctorRequest){
        try {
            doctorService.addDoctor(doctorRequest);
        }catch(InvalidMedicalSpecialization e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping
    public List<DoctorBriefResponse> getDoctors(){
        return doctorService.getDoctors();
    }

    @GetMapping("/{id}")
    public DoctorInfoResponse getDoctorById(@PathVariable Integer id) {
        try {
            return doctorService.getDoctorInfoById(id);
        } catch (DoctorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());

        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctorById(@PathVariable Integer id) {
        try {
            doctorService.deleteDoctorById(id);
        } catch (DoctorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        }
    }

}
