package pl.edu.agh.to.backendspringboot.application.patient;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.infrastructure.patient.PatientRepository;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientRequest;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientNotFoundException;

import java.util.List;

/**
 * Serwis realizujący logikę biznesową związaną z zarządzaniem pacjentami.
 * Odpowiada za komunikację między kontrolerem a warstwą danych (repozytorium),
 * a także za mapowanie obiektów domenowych na DTO.
 */
@Service
public class PatientService {
    private final PatientRepository patientRepository;

    /**
     * Konstruktor serwisu wstrzykujący zależność repozytorium.
     *
     * @param patientRepository Repozytorium umożliwiające operacje na bazie danych pacjentów.
     */
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Tworzy i zapisuje nowego pacjenta w systemie.
     * Metoda konwertuje obiekt żądania (DTO) na encję domenową przed zapisem.
     *
     * @param patientRequest Obiekt zawierający dane niezbędne do utworzenia pacjenta.
     */
    public void addPatient(PatientRequest patientRequest) {
        // SPRAWDZENIE UNIKALNOŚCI:
        if (patientRepository.existsByPesel(patientRequest.pesel())) {
            throw new PatientAlreadyExistsException("Patient with PESEL " + patientRequest.pesel() + " already exists");
        }

        patientRepository.save(PatientRequest.toEntity(patientRequest));
    }

    /**
     * Pobiera listę wszystkich pacjentów w formacie skróconym.
     * Dane są pobierane z repozytorium, a następnie mapowane na obiekty {@link PatientBriefResponse}.
     *
     * @return Lista skróconych informacji o pacjentach.
     */
    public List<PatientBriefResponse> getPatients() {
        // Używam findAll(), zakładając że nie masz jeszcze dedykowanej metody findPatientsBrief() w repozytorium
        // Jeśli masz, zamień findAll() na findPatientsBrief()
        return patientRepository.findAll().stream()
                .map(PatientBriefResponse::from)
                .toList();
    }

    /**
     * Pobiera szczegółowe informacje o pacjencie na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator pacjenta.
     * @return Obiekt {@link PatientDetailResponse} zawierający pełne dane pacjenta.
     * @throws PatientNotFoundException jeśli pacjent o podanym identyfikatorze nie zostanie znaleziony.
     */
    public PatientDetailResponse getPatientInfoById(Integer id) {
        return patientRepository.findById(id)
                .map(PatientDetailResponse::from)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));
    }

    /**
     * Usuwa pacjenta z systemu na podstawie jego identyfikatora.
     * Przed usunięciem następuje weryfikacja, czy pacjent o danym ID istnieje.
     *
     * @param id Unikalny identyfikator pacjenta do usunięcia.
     * @throws PatientNotFoundException jeśli pacjent o podanym identyfikatorze nie istnieje.
     */
    public void deletePatientById(Integer id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient with id " + id + " not found");
        }
        patientRepository.deleteById(id);
    }
}