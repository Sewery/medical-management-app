package pl.edu.agh.to.backendspringboot.presentation.schedule;

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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.to.backendspringboot.application.schedule.ScheduleService;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.ConflictInScheduleTimePeriod;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.InvalidScheduleTimePeriod;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.ScheduleNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.VisitAssignedToScheduleException;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.AvailabilityResponse;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.ScheduleDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.ScheduleRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("schedules")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Schedule Controller", description = "API do zarządzania dyżurami lekarzy (Data i Czas)")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Pobiera listę wszystkich dyżurów w formacie skróconym.
     *
     * @return Lista obiektów {@link ScheduleDetailResponse}.
     */
    @Operation(summary = "Pobierz wszystkie dyżury", description = "Zwraca listę wszystkich dyżurów lekarzy (widok skrócony).")
    @GetMapping
    public List<ScheduleDetailResponse> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    /**
     * Pobiera listę dostępnych lekarzy i gabinetów w określonym przedziale czasowym (Data + Czas).
     * <p>
     * Metoda sprawdza dostępność zasobów w podanym oknie czasowym.
     *
     * @param startTime Data i czas rozpoczęcia szukanego okna (format ISO-8601, np. 2026-01-20T08:00:00).
     * @param endTime   Data i czas zakończenia szukanego okna (format ISO-8601, np. 2026-01-20T12:00:00).
     * @return Obiekt zawierający listy wolnych lekarzy i gabinetów.
     */
    @Operation(
            summary = "Pobierz dostępnych lekarzy i gabinety",
            description = "Zwraca listę lekarzy oraz gabinetów zabiegowych, które są wolne w podanym przedziale daty i czasu.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "startTime",
                            description = "Data i czas rozpoczęcia (ISO-8601)",
                            required = true,
                            example = "2026-01-20T08:00:00",
                            schema = @Schema(type = "string", format = "date-time")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "endTime",
                            description = "Data i czas zakończenia (ISO-8601)",
                            required = true,
                            example = "2026-01-20T12:00:00",
                            schema = @Schema(type = "string", format = "date-time")
                    )
            }
    )
    @GetMapping("/availability")
    public AvailabilityResponse getAvailableDoctorsAndConsultingRooms(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        try {
            return scheduleService.getAvailableDoctorsAndConsultingRooms(startTime, endTime);
        } catch (InvalidScheduleTimePeriod e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    /**
     * Planuje nowy dyżur dla lekarza w konkretnym gabinecie na określony dzień i godzinę.
     * <p>
     * Metoda deleguje walidację reguł biznesowych do serwisu.
     *
     * @param scheduleRequest Obiekt DTO zawierający ID lekarza, ID gabinetu oraz ramy czasowe dyżuru (ISO-8601).
     */
    @Operation(
            summary = "Zaplanuj dyżur lekarza",
            description = "Tworzy nowy dyżur dla wybranego lekarza i gabinetu w określonym dniu i godzinach (format ISO-8601).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dane nowego dyżuru (pamiętaj o pełnym formacie daty!)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleRequest.class),
                            examples = @ExampleObject(
                                    name = "Przykład poprawnego dyżuru",
                                    value = """
                                            {
                                              "doctorId": 1,
                                              "consultingRoomId": 101,
                                              "startTime": "2026-01-20T08:00:00",
                                              "endTime": "2026-01-20T16:00:00"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dyżur został pomyślnie utworzony"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane (zły format daty, termin z przeszłości, zbyt odległy termin)"),
            @ApiResponse(responseCode = "409", description = "Konflikt - lekarz lub gabinet jest już zajęty w tym terminie")
    })
    @PostMapping
    public void scheduleDuty(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        try {
            scheduleService.addSchedule(scheduleRequest);
        } catch (DoctorNotFoundException | ConsultingRoomNotFoundException | InvalidScheduleTimePeriod e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        } catch (ConflictInScheduleTimePeriod e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getLocalizedMessage());
        }
    }

    /**
     * Usuwa istniejący dyżur z harmonogramu.
     *
     * @param id Unikalny identyfikator dyżuru do usunięcia.
     */
    @Operation(
            summary = "Usuń dyżur",
            description = "Trwale usuwa dyżur z systemu na podstawie jego identyfikatora. " +
                    "Operacja zostanie zablokowana, jeśli do tego dyżuru są już przypisane wizyty pacjentów."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dyżur został pomyślnie usunięty"),
            @ApiResponse(responseCode = "404", description = "Dyżur o podanym ID nie został znaleziony"),
            @ApiResponse(responseCode = "409", description = "Nie można usunąć dyżuru - istnieją przypisane do niego wizyty")
    })
    @DeleteMapping("/{id}")
    public void deleteScheduleById(@PathVariable int id) {
        try {
            scheduleService.deleteScheduleById(id);
        } catch (ScheduleNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (VisitAssignedToScheduleException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}