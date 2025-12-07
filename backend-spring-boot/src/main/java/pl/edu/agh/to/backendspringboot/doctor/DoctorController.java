package pl.edu.agh.to.backendspringboot.doctor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.doctor.exception.InvalidMedicalSpecialization;
import pl.edu.agh.to.backendspringboot.doctor.model.*;

import java.util.List;

@RestController
@RequestMapping("doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public void addDoctor(@Valid @RequestBody DoctorRequest doctorRequest){
        try {
            doctorService.addDoctor(DoctorRequest.toEntity(doctorRequest));
        }catch(InvalidMedicalSpecialization e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping
    public List<DoctorBriefResponse> getDoctors(){
        return doctorService.getDoctors().stream().map(DoctorBriefResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DoctorInfoResponse getDoctorById(@PathVariable Integer id) {
        try {
            DoctorInfo doctor = doctorService.getDoctorInfoById(id);
            return DoctorInfoResponse.from(doctor);
        } catch (DoctorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctorById(@PathVariable Integer id){
        if(!doctorService.deleteDoctorById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor with id "+id+" not found");
        }
        return new ResponseEntity<>("Doctor with id "+id+" deleted successfully", HttpStatus.OK);

    }

    public record DoctorRequest(
            @NotBlank(message = "First name is mandatory") String firstName,
            @NotBlank(message = "Last name is mandatory") String lastName,
            @Pattern(regexp = "^\\d{11}$", message = "Invalid PESEL") String pesel,
            @NotNull String specialization,
            @NotBlank(message = "Street is mandatory") String street,
            @NotBlank(message = "Street is mandatory") String city,
            @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Invalid postal code") String postalCode
    ) {
        public static Doctor toEntity(DoctorRequest doctorDTO) {
            try {
                return new Doctor(
                        doctorDTO.firstName,
                        doctorDTO.lastName,
                        doctorDTO.pesel,
                        new Address(
                                doctorDTO.street,
                                doctorDTO.city,
                                doctorDTO.postalCode
                        ),
                        MedicalSpecialization.valueOf(doctorDTO.specialization.toUpperCase())
                );
            } catch (IllegalArgumentException e) {
                throw new InvalidMedicalSpecialization("Invalid medical specialization");
            }
        }

    }
    public record DoctorBriefResponse(
            String firstName,
            String lastName,
            String specialization
    ){
        public static DoctorBriefResponse from(DoctorBrief doctor){
            return new DoctorBriefResponse(
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctor.getSpecialization().toString()
            );
        }
    }

    public record DoctorInfoResponse(
            String firstName,
            String lastName,
            String specialization,
            String postalCode,
            String street,
            String city
    ){
        public static DoctorInfoResponse from(DoctorInfo doctor){
            return new DoctorInfoResponse(
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctor.getSpecialization().toString(),
                    doctor.getAddress().getPostalCode(),
                    doctor.getAddress().getStreet(),
                    doctor.getAddress().getCity()
            );
        }
    }




}
