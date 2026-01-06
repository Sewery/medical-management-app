package pl.edu.agh.to.backendspringboot.presentation.consulting_room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.application.consulting_room.ConsultingRoomService;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNumberAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomRequest;

import java.util.List;

@RestController
@RequestMapping("consulting-room")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Consulting Room Controller", description = "API do zarządzania danymi gabinetu lekarskiego")
public class ConsultingRoomController {
    private final ConsultingRoomService consultingRoomService;
    /**
     * Konstruktor kontrolera wstrzykujący zależność serwisu gabinetów lekarskich.
     *
     * @param consultingRoomService Serwis zawierający logikę biznesową dotyczącą gabinetów lekarskich.
     */
    public ConsultingRoomController(ConsultingRoomService consultingRoomService){
        this.consultingRoomService = consultingRoomService;
    }

    /**
     * Dodaje nowy gabinet lekarski do systemu.
     * <p>
     * Metoda waliduje poprawność danych wejściowych (np. czy podano numer pokoju).
     *
     * @param consultingRoomRequest Obiekt DTO zawierający dane wymagane do utworzenia gabinetu.
     */
    @Operation(
            summary = "Dodaj nowy gabinet lekarski",
            description = "Tworzy rekord gabinetu zabiegowego w bazie danych. " +
                    "Wymaga podania numeru pokoju. Pozostałe pola określają wyposażenie i są opcjonalne (domyślnie false)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gabinet lekarski został pomyślnie dodany"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych (np. brak numeru pokoju)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Konflikt - gabinet o takim numerze już istnieje", content = @Content)
    })
    @PostMapping
    public void addConsultingRoom(@Valid @RequestBody ConsultingRoomRequest consultingRoomRequest){
        try {
            consultingRoomService.addConsultingRoom(consultingRoomRequest);
        }catch(ConsultingRoomNumberAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getLocalizedMessage());
        }
    }

    /**
     * Pobiera listę wszystkich gabinetów lekarskich dostępnych w systemie.
     * Zwraca dane w formie skróconej (Brief).
     *
     * @return Lista obiektów {@link ConsultingRoomBriefResponse} reprezentujących gabinety.
     */
    @Operation(summary = "Pobierz listę gabinetów lekarskich", description = "Zwraca listę wszystkich gabinetów lekarskich w systemie w formacie skróconym.")
    @ApiResponse(responseCode = "200", description = "Lista gabinetów lekarskich została pobrana")
    @GetMapping
    public List<ConsultingRoomBriefResponse> getConsultingRooms(){
        return consultingRoomService.getConsultingRooms();
    }

    /**
     * Pobiera szczegółowe informacje o konkretnym gabinecie lekarskim na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator gabinetu lekarskiego.
     * @return Obiekt {@link ConsultingRoomDetailResponse} zawierający szczegółowe dane o gabinecie lekarskim.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND), jeśli gabinet o podanym ID nie istnieje.
     */
    @Operation(summary = "Pobierz szczegóły o gabinecie lekarskim", description = "Zwraca pełne informacje o gabinecie lekarskim (numer pokoju, dostępny sprzęt medyczny, terminy dyżurów lekarskich) na podstawie jego ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono gabinet lekarski"),
            @ApiResponse(responseCode = "404", description = "Gabinet lekarski o podanym ID nie istnieje", content = @Content)
    })
    @GetMapping("/{id}")
    public ConsultingRoomDetailResponse getConsultingRoomDetailById(@PathVariable Integer id){
        try {
            return consultingRoomService.getConsultingRoomDetailResponse(id);
        } catch (ConsultingRoomNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        }
    }

    /**
     * Usuwa gabinet lekarski z systemu na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator gabinet lekarskiego do usunięcia.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND), jeśli gabinet lekarski o podanym ID nie istnieje.
     */
    @Operation(summary = "Usuń gabinet lekarski", description = "Usuwa gabinet lekarski z bazy danych na podstawie podanego ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gabinet lekarski został usunięty (brak zawartości)"),
            @ApiResponse(responseCode = "404", description = "Gabinet lekarski o podanym ID nie istnieje", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsultingRoomById(@PathVariable Integer id){
        try {
            consultingRoomService.deleteConsultingRoomById(id);
        } catch (ConsultingRoomNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        }
    }


}
