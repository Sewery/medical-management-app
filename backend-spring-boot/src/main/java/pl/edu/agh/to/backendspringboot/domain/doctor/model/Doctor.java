package pl.edu.agh.to.backendspringboot.domain.doctor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;

import java.util.Set;

@Entity
public class Doctor {
    @Id
    @GeneratedValue
    private Integer id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String pesel;

    @Embedded
    @NotNull
    private Address address;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MedicalSpecialization specialization;

    public Doctor() {
    }

    public Doctor(String firstName, String lastName, String pesel, Address address, MedicalSpecialization specialization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.address = address;
        this.specialization = specialization;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public Address getAddress() {
        return address;
    }

    public MedicalSpecialization getSpecialization() {
        return specialization;
    }
}
