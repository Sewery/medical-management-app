package pl.edu.agh.to.backendspringboot.presentation.doctor.dto;

import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorBrief;

public record DoctorBriefResponse(
        Integer id,
        String firstName,
        String lastName,
        String specialization
){
    public static DoctorBriefResponse from(Doctor doctor) {
        return new DoctorBriefResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization().toString()
        );
    }
    public static DoctorBriefResponse from(DoctorBrief doctor){
        return new DoctorBriefResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization().toString()
        );
    }
}
