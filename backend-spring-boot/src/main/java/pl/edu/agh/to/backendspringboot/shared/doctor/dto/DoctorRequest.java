package pl.edu.agh.to.backendspringboot.shared.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.InvalidMedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Address;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;

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