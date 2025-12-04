package pl.edu.agh.to.backendspringboot.doctor;

import jakarta.persistence.*;

@Entity
public class Doctor {
    @Id
    @GeneratedValue
    private Integer id;

    private String firstName;
    private String lastName;
    private String pesel;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
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
