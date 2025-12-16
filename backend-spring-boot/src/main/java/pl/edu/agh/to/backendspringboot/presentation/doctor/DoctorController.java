package pl.edu.agh.to.backendspringboot.presentation.doctor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.application.doctor.DoctorService;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorInfoResponse;
import pl.edu.agh.to.backendspringboot.shared.doctor.dto.DoctorRequest;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.InvalidMedicalSpecialization;

import java.util.List;
/**
 * Kontroler REST obsługujący operacje związane z zarządzaniem lekarzami.
 * Udostępnia endpointy do tworzenia, pobierania i usuwania lekarzy.
 */
@RestController
@RequestMapping("doctors")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Doctor Controller", description = "API do zarządzania danymi lekarzy")
public class DoctorController {
    private final DoctorService doctorService;
    
    /**
     * Konstruktor kontrolera wstrzykujący zależność serwisu lekarzy.
     *
     * @param doctorService Serwis zawierający logikę biznesową dotyczącą lekarzy.
     */
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Dodaje nowego lekarza do systemu.
     * <p>
     * Metoda waliduje poprawność danych wejściowych. Jeśli specjalizacja medyczna
     * jest nieprawidłowa, zwracany jest błąd 400 Bad Request.
     *
     * @param doctorRequest Obiekt DTO zawierający dane wymagane do utworzenia lekarza.
     * @throws ResponseStatusException (HttpStatus.BAD_REQUEST) jeśli podana specjalizacja jest nieprawidłowa.
     */
    @Operation(
            summary = "Dodaj nowego lekarza",
            description = "Tworzy rekord lekarza w bazie danych. " +
                    "Wymaga podania poprawnej specjalizacji medycznej. " +
                    "**Dostępne specjalizacje (przykłady):** CARDIOLOGIST, DENTIST, DERMATOLOGIST, ENDOCRINOLOGIST, " +
                    "GASTROENTEROLOGIST, GYNECOLOGIST, NEUROLOGIST, ONCOLOGIST, OPHTHALMOLOGIST, PEDIATRICIAN, " +
                    "PSYCHIATRIST, SURGEON, UROLOGIST."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lekarz został pomyślnie dodany"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowa specjalizacja medyczna lub błąd walidacji danych",
                    content = @Content)
    })
    @PostMapping
    public void addDoctor(@Valid @RequestBody DoctorRequest doctorRequest){
        try {
            doctorService.addDoctor(doctorRequest);
        }catch(InvalidMedicalSpecialization e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    /**
     * Pobiera listę wszystkich lekarzy dostępnych w systemie.
     * Zwraca dane w formie skróconej (Brief).
     *
     * @return Lista obiektów {@link DoctorBriefResponse} reprezentujących lekarzy.
     */
    @Operation(summary = "Pobierz listę lekarzy", description = "Zwraca listę wszystkich lekarzy w systemie w formacie skróconym.")
    @ApiResponse(responseCode = "200", description = "Lista lekarzy została pobrana")
    @GetMapping
    public List<DoctorBriefResponse> getDoctors(){
        return doctorService.getDoctors();
    }

    /**
     * Pobiera szczegółowe informacje o konkretnym lekarzu na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator lekarza.
     * @return Obiekt {@link DoctorInfoResponse} zawierający szczegółowe dane lekarza.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) jeśli lekarz o podanym ID nie istnieje.
     */
    @Operation(summary = "Pobierz szczegóły lekarza", description = "Zwraca pełne informacje o lekarzu na podstawie jego ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono lekarza"),
            @ApiResponse(responseCode = "404", description = "Lekarz o podanym ID nie istnieje", content = @Content)
    })
    @GetMapping("/{id}")
    public DoctorInfoResponse getDoctorById(@PathVariable Integer id) {
        try {
            return doctorService.getDoctorInfoById(id);
        } catch (DoctorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        }
    }

    /**
     * Usuwa lekarza z systemu na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator lekarza do usunięcia.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) jeśli lekarz o podanym ID nie istnieje.
     */
    @Operation(summary = "Usuń lekarza", description = "Usuwa lekarza z bazy danych na podstawie podanego ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lekarz został usunięty (brak zawartości)"),
            @ApiResponse(responseCode = "404", description = "Lekarz o podanym ID nie istnieje", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctorById(@PathVariable Integer id) {
        try {
            doctorService.deleteDoctorById(id);
        } catch (DoctorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        }
    }

}
