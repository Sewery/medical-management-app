package pl.edu.agh.to.backendspringboot.presentation.visit;

import io.reactivex.rxjava3.core.Observable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.application.visit.VisitService;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitNotInScheduleException;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.AvailabilityResponse;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.VisitBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.VisitDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.VisitRequest;

import java.util.List;

@RestController
@RequestMapping("visits")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Visit Controller", description = "API do zarządzania wizytami lekarskimi i sprawdzania dostępności")
public class VisitController {

    private final VisitService visitService;

    /**
     * Konstruktor kontrolera wstrzykujący zależność serwisu wizyt.
     * <p>
     * Uwaga: Walidacja reguł biznesowych (np. maksymalne wyprzedzenie czasowe)
     * odbywa się w warstwie serwisu (VisitService).
     *
     * @param visitService Serwis realizujący logikę biznesową wizyt.
     */
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    /**
     * Wyszukuje dostępne terminy wizyt dla podanej specjalizacji lekarskiej.
     * <p>
     * Metoda asynchronicznie agreguje wolne sloty czasowe (data i godzina) wszystkich lekarzy
     * danej specjalizacji.
     *
     * @param specialization Nazwa specjalizacji (np. "CARDIOLOGY"). Wielkość liter nie ma znaczenia.
     * @return Strumień (Observable) zawierający listę dostępnych terminów (AvailabilityResponse).
     */
    @Operation(
            summary = "Sprawdź dostępność wizyt",
            description = "Zwraca listę wolnych terminów wizyt (Data i Czas) dla wszystkich lekarzy o wskazanej specjalizacji. " +
                    "Wyniki są filtrowane przez serwis pod kątem maksymalnego czasu wyprzedzenia.",
            parameters = @Parameter(
                    name = "specialization",
                    description = "Specjalizacja lekarska (np. Cardiology)",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "Cardiology"
            )
    )
    @GetMapping("/availability")
    public Observable<List<AvailabilityResponse>> getAvailability(@RequestParam String specialization) {
        MedicalSpecialization medicalSpecialization = MedicalSpecialization.getEnum(specialization);
        return visitService.getPossibleVisits(medicalSpecialization).toList().toObservable();
    }

    /**
     * Pobiera listę wszystkich zaplanowanych wizyt w systemie.
     *
     * @return Lista obiektów {@link VisitBriefResponse} zawierających skrócone informacje o wizytach.
     */
    @Operation(summary = "Pobierz wszystkie wizyty", description = "Zwraca pełną listę zarejestrowanych wizyt w systemie (widok skrócony).")
    @GetMapping
    public List<VisitBriefResponse> getAllVisits() {
        return visitService.getAllVisits().stream().map(VisitBriefResponse::from).toList();
    }

    /**
     * Pobiera szczegółowe informacje o konkretnej wizycie.
     *
     * @param id Unikalny identyfikator wizyty.
     * @return Obiekt {@link VisitDetailResponse} ze szczegółami wizyty.
     */
    @Operation(summary = "Pobierz szczegóły wizyty", description = "Zwraca pełne dane wizyty (w tym dane pacjenta, lekarza i gabinetu) na podstawie ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono wizytę"),
            @ApiResponse(responseCode = "404", description = "Wizyta o podanym ID nie istnieje")
    })
    @GetMapping("/{id}")
    public VisitDetailResponse getVisitById(@PathVariable int id) {
        try {
            return VisitDetailResponse.from(visitService.getVisitById(id));
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Anuluje (usuwa) wizytę z systemu.
     *
     * @param id Identyfikator wizyty do usunięcia.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) jeśli wizyta nie istnieje.
     */
    @Operation(summary = "Anuluj wizytę", description = "Trwale usuwa wizytę z systemu na podstawie jej identyfikatora.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wizyta została pomyślnie anulowana"),
            @ApiResponse(responseCode = "404", description = "Wizyta nie została znaleziona")
    })
    @DeleteMapping("/{id}")
    public void deleteVisitById(@PathVariable int id) {
        try {
            visitService.deleteVisitById(id);
        } catch (VisitNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Rejestruje nową wizytę w systemie na określony dzień i godzinę.
     * <p>
     * Metoda deleguje walidację do serwisu, który sprawdza:
     * <ul>
     * <li>Czy termin jest poprawny (nie przeszły, nie zbyt odległy).</li>
     * <li>Czy zasoby (lekarz, pacjent, gabinet) istnieją.</li>
     * <li>Czy nie ma konfliktów (zajęte terminy).</li>
     * <li>Czy termin pokrywa się z harmonogramem lekarza.</li>
     * </ul>
     *
     * @param visitDataRequest Dane nowej wizyty (ID zasobów, data i czas w formacie ISO-8601).
     * @throws ResponseStatusException Kod błędu zależny od naruszonej reguły biznesowej.
     */
    @Operation(
            summary = "Umów wizytę",
            description = "Rejestruje nową wizytę dla pacjenta u wybranego lekarza w konkretnym terminie. " +
                    "Wymagany format daty: ISO-8601 (np. '2026-01-20T10:30:00').",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Szczegóły wizyty. Upewnij się, że data mieści się w dozwolonym limicie czasowym.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VisitRequest.class),
                            examples = @ExampleObject(
                                    name = "Przykład poprawnej wizyty",
                                    value = """
                                            {
                                              "doctorId": 1,
                                              "patientId": 1,
                                              "consultingRoomId": 101,
                                              "visitStart": "2026-01-20T10:00:00",
                                              "visitEnd": "2026-01-20T10:30:00"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wizyta została pomyślnie umówiona"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji: termin z przeszłości, termin zbyt odległy (limit dni) lub brak dyżuru lekarza"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono lekarza, pacjenta lub gabinetu"),
            @ApiResponse(responseCode = "409", description = "Konflikt: Wybrany termin jest już zajęty")
    })
    @PostMapping
    public void addVisit(@Valid @RequestBody VisitRequest visitDataRequest) {
        try {
            visitService.addVisit(visitDataRequest);
        } catch (DoctorNotFoundException | PatientNotFoundException | ConsultingRoomNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VisitAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (VisitNotInScheduleException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}