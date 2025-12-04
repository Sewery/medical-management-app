package pl.edu.agh.to.backendspringboot.doctor;

public enum MedicalSpecialization {

    INTERNAL_MEDICINE("Internal Medicine"),
    FAMILY_MEDICINE("Family Medicine"),
    PEDIATRICS("Pediatrics"),
    ALLERGOLOGY("Allergology"),
    ANESTHESIOLOGY("Anesthesiology"),
    CARDIOLOGY("Cardiology"),
    DERMATOLOGY("Dermatology"),
    ENDOCRINOLOGY("Endocrinology"),
    GASTROENTEROLOGY("Gastroenterology"),
    GENERAL_SURGERY("General Surgery"),
    GYNECOLOGY("Gynecology"),
    NEUROLOGY("Neurology"),
    ONCOLOGY("Oncology"),
    OPHTHALMOLOGY("Ophthalmology"),
    ORTHOPEDICS("Orthopedics"),
    OTOLARYNGOLOGY("Otolaryngology"),
    PSYCHIATRY("Psychiatry"),
    PULMONOLOGY("Pulmonology"),
    RADIOLOGY("Radiology"),
    RHEUMATOLOGY("Rheumatology"),
    UROLOGY("Urology"),
    DENTISTRY("Dentistry"),
    NONE("None");

    private final String name;

    MedicalSpecialization(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}