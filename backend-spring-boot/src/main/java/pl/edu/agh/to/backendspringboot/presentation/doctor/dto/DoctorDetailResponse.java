package pl.edu.agh.to.backendspringboot.presentation.doctor.dto;

import pl.edu.agh.to.backendspringboot.domain.doctor.model.DoctorDetail;

public record DoctorDetailResponse(
        Integer id,
        String firstName,
        String lastName,
        String specialization,
        String pesel,
        String postalCode,
        String street,
        String city
){
    public static DoctorDetailResponse from(DoctorDetail doctor){
        return new DoctorDetailResponse(
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