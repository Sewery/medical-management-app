package pl.edu.agh.to.backendspringboot.doctor.model;

public enum MedicalSpecialization {

    INTERNAL_MEDICINE("Choroby wewnętrzne"),
    FAMILY_MEDICINE("Medycyna rodzinna"),
    PEDIATRICS("Pediatria"),
    ALLERGOLOGY("Alergologia"),
    ANESTHESIOLOGY("Anestezjologia"),
    CARDIOLOGY("Kardiologia"),
    DERMATOLOGY("Dermatologia"),
    ENDOCRINOLOGY("Endokrynologia"),
    GASTROENTEROLOGY("Gastroenterologia"),
    GENERAL_SURGERY("Chirurgia ogólna"),
    GYNECOLOGY("Ginekologia"),
    NEUROLOGY("Neurologia"),
    ONCOLOGY("Onkologia"),
    OPHTHALMOLOGY("Okulistyka"),
    ORTHOPEDICS("Ortopedia"),
    OTOLARYNGOLOGY("Otolaryngologia"), // Laryngologia
    PSYCHIATRY("Psychiatria"),
    PULMONOLOGY("Pulmonologia"),
    RADIOLOGY("Radiologia"),
    RHEUMATOLOGY("Reumatologia"),
    UROLOGY("Urologia"),
    DENTISTRY("Stomatologia"),
    NONE("Brak"); // Dodana polska nazwa dla NONE

    private final String name;

    MedicalSpecialization(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}