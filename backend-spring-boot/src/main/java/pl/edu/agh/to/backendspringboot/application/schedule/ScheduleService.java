package pl.edu.agh.to.backendspringboot.application.schedule;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.to.backendspringboot.application.shared.DateValidator;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.ConflictInScheduleTimePeriod;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.InvalidScheduleTimePeriod;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.ScheduleNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.schedule.exception.VisitAssignedToScheduleException;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.Schedule;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.visit.VisitRepository;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.AvailabilityResponse;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.ScheduleDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.schedule.dto.ScheduleRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serwis realizujący logikę biznesową związaną z harmonogramem wizyt i dyżurów.
 * Odpowiada za planowanie terminów, sprawdzanie dostępności zasobów (lekarzy i gabinetów)
 * oraz walidację konfliktów czasowych.
 */
@Service
public class ScheduleService {

    private static final Duration MIN_SHIFT_DURATION = Duration.ofMinutes(30);
    private static final Duration MAX_SHIFT_DURATION = Duration.ofHours(12);

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final ConsultingRoomRepository consultingRoomRepository;
    private final VisitRepository visitRepository;
    private final DateValidator dateValidator;

    public ScheduleService(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, ConsultingRoomRepository consultingRoomRepository, VisitRepository visitRepository, DateValidator dateValidator) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.consultingRoomRepository = consultingRoomRepository;
        this.visitRepository = visitRepository;
        this.dateValidator = dateValidator;
    }

    /**
     * Pobiera listę wszystkich dyżurów zarejestrowanych w systemie.
     */
    public List<ScheduleDetailResponse> getAllSchedules() {
        return scheduleRepository.findAllScheduleDetails().stream().map(ScheduleDetailResponse::from).toList();
    }

    /**
     * Wyszukuje lekarzy i gabinety dostępne w zadanym przedziale czasowym (Data + Czas).
     *
     * @param startTime Data i godzina rozpoczęcia szukanego okna czasowego.
     * @param endTime Data i godzina zakończenia szukanego okna czasowego.
     */
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailableDoctorsAndConsultingRooms(LocalDateTime startTime, LocalDateTime endTime) {
        validateScheduleTimePeriod(startTime, endTime);
        dateValidator.validateDateRange(startTime);

        var doctors = scheduleRepository.findAvailableDoctorsInPeriod(startTime, endTime)
                .stream().map(DoctorBriefResponse::from).toList();

        var consultingRooms = scheduleRepository.findAvailableConsultingRoomsInPeriod(startTime, endTime)
                .stream().map(ConsultingRoomBriefResponse::from).toList();

        return new AvailabilityResponse(doctors, consultingRooms);
    }

    /**
     * Tworzy i zapisuje nowy dyżur (Schedule) w systemie.
     */
    @Transactional
    public void addSchedule(ScheduleRequest scheduleRequest) {
        validateScheduleTimePeriod(scheduleRequest.startTime(), scheduleRequest.endTime());
        dateValidator.validateDateRange(scheduleRequest.startTime());

        int doctorId = scheduleRequest.doctorId();
        int consultingRoomId = scheduleRequest.consultingRoomId();

        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + doctorId + " not found"));
        var consultingRoom = consultingRoomRepository.findById(consultingRoomId)
                .orElseThrow(() -> new ConsultingRoomNotFoundException("Consulting room with id " + consultingRoomId + " not found"));

        if (scheduleRepository.existsScheduleInPeriodForConsultingDoctor(scheduleRequest.startTime(), scheduleRequest.endTime(), consultingRoomId)) {
            throw new ConflictInScheduleTimePeriod("Doctor is already scheduled for this consulting room");
        }

        if (scheduleRepository.existsScheduleInPeriodForDoctor(scheduleRequest.startTime(), scheduleRequest.endTime(), doctorId)) {
            throw new ConflictInScheduleTimePeriod("Consulting room is already scheduled for this doctor");
        }

        scheduleRepository.save(scheduleRequest.toSchedule(doctor, consultingRoom));
    }

    /**
     * Usuwa wybrany dyżur z systemu na podstawie jego identyfikatora.
     */
    public void deleteScheduleById(int scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ScheduleNotFoundException("Schedule with id " + scheduleId + " does not exist");
        }
        Schedule schedule = scheduleRepository.findById(scheduleId).get();

        if (visitRepository.visitExistsForSchedule(schedule.getDoctor().getId(), schedule.getShiftStart(), schedule.getShiftEnd())) {
            throw new VisitAssignedToScheduleException("Cannot delete schedule with assigned visits");
        }
        scheduleRepository.deleteById(scheduleId);
    }

    /**
     * Metoda pomocnicza walidująca poprawność logiczną przedziału czasowego.
     * Sprawdza kolejność dat oraz minimalną i maksymalną długość trwania dyżuru używając Duration.
     *
     * @param startTime Data i godzina rozpoczęcia.
     * @param endTime Data i godzina zakończenia.
     * @throws InvalidScheduleTimePeriod jeśli walidacja nie powiedzie.
     */
    private void validateScheduleTimePeriod(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new InvalidScheduleTimePeriod("Start time and end time cannot be null");
        }

        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new InvalidScheduleTimePeriod("End time must be after start time");
        }

        Duration shiftDuration = Duration.between(startTime, endTime);

        if (shiftDuration.compareTo(MAX_SHIFT_DURATION) > 0) {
            throw new InvalidScheduleTimePeriod("Shift period must be less than " + MAX_SHIFT_DURATION.toHours() + " hours");
        }

        if (shiftDuration.compareTo(MIN_SHIFT_DURATION) < 0) {
            throw new InvalidScheduleTimePeriod("Shift period must be greater than " + MIN_SHIFT_DURATION.toMinutes() + " minutes");
        }
    }
}