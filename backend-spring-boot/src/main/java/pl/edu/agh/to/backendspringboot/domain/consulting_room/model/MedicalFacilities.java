package pl.edu.agh.to.backendspringboot.domain.consulting_room.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MedicalFacilities {
    @Column(nullable = false)
    private boolean hasExaminationBed; // Czy gabinet ma łóżko do badań

    @Column(nullable = false)
    private boolean hasECGMachine; // Czy gabinet ma aparat EKG

    @Column(nullable = false)
    private boolean hasScale; // Czy gabinet ma wagę

    @Column(nullable = false)
    private boolean hasThermometer; // Czy gabinet ma termometr

    @Column(nullable = false)
    private boolean hasDiagnosticSet; // Zestaw diagnostyczny (otoskop, oftalmoskop)

    public MedicalFacilities(boolean hasExaminationBed, boolean hasECGMachine, boolean hasScale, boolean hasThermometer, boolean hasDiagnosticSet) {
        this.hasExaminationBed = hasExaminationBed;
        this.hasECGMachine = hasECGMachine;
        this.hasScale = hasScale;
        this.hasThermometer = hasThermometer;
        this.hasDiagnosticSet = hasDiagnosticSet;
    }

    public MedicalFacilities() {

    }

    public boolean isHasExaminationBed() {
        return hasExaminationBed;
    }

    public boolean isHasECGMachine() {
        return hasECGMachine;
    }

    public boolean isHasScale() {
        return hasScale;
    }

    public boolean isHasThermometer() {
        return hasThermometer;
    }

    public boolean isHasDiagnosticSet() {
        return hasDiagnosticSet;
    }
}
