package pl.edu.agh.to.backendspringboot.shared.doctor.dto;

import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorInfo;

public record DoctorInfoResponse(
        Integer id,
        String firstName,
        String lastName,
        String specialization,
        String pesel,
        String postalCode,
        String street,
        String city
){
    public static DoctorInfoResponse from(DoctorInfo doctor){
        return new DoctorInfoResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization().toString(),
                doctor.getPesel(),
                doctor.getAddress().getPostalCode(),
                doctor.getAddress().getStreet(),
                doctor.getAddress().getCity()
        );
    }
}