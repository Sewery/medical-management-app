package pl.edu.agh.to.backendspringboot.presentation.patient.dto;

import pl.edu.agh.to.backendspringboot.domain.patient.model.PatientBrief;

public record PatientBriefResponse(
        Integer id,
        String firstName,
        String lastName
) {
    public static PatientBriefResponse from(PatientBrief patient) {
        return new PatientBriefResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName()
        );
    }
}