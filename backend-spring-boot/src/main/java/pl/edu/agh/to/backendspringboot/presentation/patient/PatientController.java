package pl.edu.agh.to.backendspringboot.presentation.patient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.application.patient.PatientService;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientNotFoundException;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.patient.dto.PatientRequest;

import java.util.List;

@RestController
@RequestMapping("patients")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Patient Controller", description = "API do zarządzania pacjentami")
public class PatientController {

    private final PatientService patientService;

    /**
     * Konstruktor kontrolera wstrzykujący zależność serwisu pacjentów.
     *
     * @param patientService Serwis zawierający logikę biznesową dotyczącą pacjentów.
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Pobiera listę wszystkich pacjentów w formacie skróconym.
     *
     * @return Lista obiektów {@link PatientBriefResponse}.
     */
    @Operation(summary = "Pobierz wszystkich pacjentów", description = "Zwraca listę wszystkich zarejestrowanych pacjentów (widok skrócony).")
    @GetMapping
    public List<PatientBriefResponse> getPatients() {
        return patientService.getPatients();
    }

    /**
     * Pobiera szczegółowe informacje o pacjencie na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator pacjenta.
     * @return Obiekt {@link PatientDetailResponse} zawierający pełne dane pacjenta.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) jeśli pacjent nie zostanie znaleziony.
     */
    @Operation(summary = "Pobierz szczegóły pacjenta", description = "Zwraca szczegółowe dane pacjenta na podstawie ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono pacjenta"),
            @ApiResponse(responseCode = "404", description = "Pacjent o podanym ID nie istnieje")
    })
    @GetMapping("/{id}")
    public PatientDetailResponse getPatientInfoById(@PathVariable Integer id) {
        try {
            return patientService.getPatientInfoById(id);
        } catch (PatientNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Dodaj pacjenta", description = "Tworzy nowego pacjenta w systemie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pacjent został pomyślnie utworzony"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "409", description = "Konflikt - pacjent z takim numerem PESEL już istnieje")
    })
    @PostMapping
    public void addPatient(@Valid @RequestBody PatientRequest patientRequest) {
        try {
            patientService.addPatient(patientRequest);
        } catch (PatientAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Usuwa pacjenta z systemu.
     *
     * @param id ID pacjenta do usunięcia.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) jeśli pacjent nie istnieje.
     */
    @Operation(summary = "Usuń pacjenta", description = "Usuwa pacjenta z bazy danych.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pacjent usunięty"),
            @ApiResponse(responseCode = "404", description = "Pacjent nie znaleziony")
    })
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Integer id) {
        try {
            patientService.deletePatientById(id);
        } catch (PatientNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}