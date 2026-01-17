package pl.edu.agh.to.backendspringboot.domain.doctor.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MedicalSpecialization {

    INTERNAL_MEDICINE("Choroby wewnętrzne",30),
    FAMILY_MEDICINE("Medycyna rodzinna"),
    PEDIATRICS("Pediatria",60),
    ALLERGOLOGY("Alergologia",45),
    ANESTHESIOLOGY("Anestezjologia"),
    CARDIOLOGY("Kardiologia",30),
    DERMATOLOGY("Dermatologia"),
    ENDOCRINOLOGY("Endokrynologia"),
    GASTROENTEROLOGY("Gastroenterologia"),
    GENERAL_SURGERY("Chirurgia ogólna",45),
    GYNECOLOGY("Ginekologia",30),
    NEUROLOGY("Neurologia"),
    ONCOLOGY("Onkologia",90),
    OPHTHALMOLOGY("Okulistyka",30),
    ORTHOPEDICS("Ortopedia"),
    OTOLARYNGOLOGY("Otolaryngologia"), // Laryngologia
    PSYCHIATRY("Psychiatria"),
    PULMONOLOGY("Pulmonologia"),
    RADIOLOGY("Radiologia"),
    RHEUMATOLOGY("Reumatologia"),
    UROLOGY("Urologia"),
    DENTISTRY("Stomatologia",30),
    NONE("Brak"); // Dodana polska nazwa dla NONE

    private final String name;

    private final int visitTime;

    MedicalSpecialization(String name, int visitTime) {
        this.name = name;
        this.visitTime = visitTime;
    }

    MedicalSpecialization(String name) {
        this.name = name;
        this.visitTime = 15;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<String>  getAllPossibleNames(){
        return Arrays.stream(MedicalSpecialization.values())
                .map(specialization -> specialization.name)
                .collect(Collectors.toList());
    }

    public int getVisitTime() {
        return visitTime;
    }

}