package pl.edu.agh.to.backendspringboot.application.visit;

import io.reactivex.rxjava3.core.Observable;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import pl.edu.agh.to.backendspringboot.application.shared.DateValidator;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.exception.ConsultingRoomNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.consulting_room.model.ConsultingRoom;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.Doctor;
import pl.edu.agh.to.backendspringboot.domain.doctor.model.MedicalSpecialization;
import pl.edu.agh.to.backendspringboot.domain.patient.exception.PatientNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.patient.model.Patient;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.ScheduleDetail;
import pl.edu.agh.to.backendspringboot.domain.visit.*;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitAlreadyExistsException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitNotFoundException;
import pl.edu.agh.to.backendspringboot.domain.visit.exception.VisitNotInScheduleException;
import pl.edu.agh.to.backendspringboot.infrastructure.consulting_room.ConsultingRoomRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.patient.PatientRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.visit.VisitRepository;
import pl.edu.agh.to.backendspringboot.presentation.consulting_room.dto.ConsultingRoomBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.AvailabilityResponse;
import pl.edu.agh.to.backendspringboot.presentation.visit.dto.VisitRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serwis realizujący logikę biznesową związaną z zarządzaniem wizytami lekarskimi.
 * Odpowiada za wyszukiwanie wolnych terminów, umawianie nowych wizyt, walidację konfliktów
 * oraz zarządzanie relacjami między pacjentem, lekarzem i gabinetem.
 */
@Service
public class VisitService {
    private final VisitRepository visitRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ConsultingRoomRepository consultingRoomRepository;
    private final DateValidator dateValidator;

    /**
     * Konstruktor serwisu wstrzykujący wymagane repozytoria.
     *
     * @param visitRepository Repozytorium do zarządzania wizytami.
     * @param scheduleRepository Repozytorium do pobierania grafików lekarzy.
     * @param doctorRepository Repozytorium do weryfikacji danych lekarzy.
     * @param patientRepository Repozytorium do weryfikacji danych pacjentów.
     * @param consultingRoomRepository Repozytorium do weryfikacji gabinetów.
     */
    public VisitService(VisitRepository visitRepository, ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, ConsultingRoomRepository consultingRoomRepository, DateValidator dateValidator) {
        this.visitRepository = visitRepository;
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.consultingRoomRepository = consultingRoomRepository;
        this.dateValidator = dateValidator;
    }

    /**
     * Generuje strumień dostępnych terminów wizyt dla danej specjalizacji lekarskiej.
     * Metoda pobiera lekarzy o danej specjalizacji, analizuje ich harmonogramy pracy,
     * dzieli czas pracy na sloty o długości odpowiadającej wizycie, a następnie filtruje
     * terminy już zajęte. Wyniki są sortowane chronologicznie i alfabetycznie (po nazwisku).
     *
     * @param specialization Specjalizacja lekarska, dla której szukane są terminy.
     * @return Strumień {@link Observable} zawierający obiekty {@link AvailabilityResponse} z wolnymi terminami.
     */
    public Observable<AvailabilityResponse> getPossibleVisits(MedicalSpecialization specialization) {
        int duration= specialization.getVisitTime();
        LocalDateTime maxDateLimit = LocalDateTime.now().plusDays(dateValidator.getMaxDaysInAdvance());
        return Observable.fromIterable(doctorRepository.findAllBySpecialization(specialization))
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .map(doctor -> scheduleRepository.findAllByDoctorIdDetail(doctor.getId()))
                .flatMapIterable(schedules -> schedules)
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.computation())
                .map(schedule -> getAllPossibleVisitsForSchedule(schedule, duration))
                .flatMapIterable(possibleVisits -> possibleVisits)
                .filter(visit -> visit.getVisitStart().isBefore(maxDateLimit))
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .filter(visit -> !visitRepository.collidingVisitExist(visit.getVisitStart(), visit.getVisitEnd(), visit.getDoctor().getId()))
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.computation())
                .sorted((v1, v2) -> {
                    if (v1.getVisitStart().equals(v2.getVisitStart())) {
                        return v1.getDoctor().getLastName().compareTo(v2.getDoctor().getLastName());
                    }
                    return v1.getVisitStart().compareTo(v2.getVisitStart());
                });
    }

    /**
     * Pobiera listę wszystkich wizyt zarejestrowanych w systemie.
     *
     * @return Lista obiektów {@link VisitBrief} reprezentujących wizyty.
     */
    public List<VisitBrief> getAllVisits(){
        return visitRepository.findAllVisits();
    }

    /**
     * Pobiera szczegółowe informacje o wizycie na podstawie jej identyfikatora.
     *
     * @param id Unikalny identyfikator wizyty.
     * @return Obiekt {@link VisitDetail} zawierający szczegóły wizyty.
     */
    public VisitDetail getVisitById(int id){
        return visitRepository.findById(id);
    }

    /**
     * Usuwa wizytę z systemu na podstawie jej identyfikatora.
     *
     * @param id Identyfikator wizyty do usunięcia.
     * @throws VisitNotFoundException jeśli wizyta o podanym ID nie istnieje.
     */
    public void deleteVisitById(int id) throws VisitNotFoundException {
        if(!visitRepository.existsById(id)){
            throw new VisitNotFoundException("Visit with id " + id + " not found.");
        }
        visitRepository.deleteById(id);
    }

    /**
     * Rejestruje nową wizytę w systemie.
     * Metoda przeprowadza kompleksową walidację: sprawdza istnienie lekarza, pacjenta i gabinetu,
     * weryfikuje czy żaden z zasobów (lekarz, pacjent, gabinet) nie ma w tym czasie innej wizyty (kolizje),
     * oraz sprawdza czy termin wizyty mieści się w harmonogramie pracy lekarza.
     *
     * @param visitDataRequest Obiekt DTO z danymi nowej wizyty.
     * @throws DoctorNotFoundException jeśli lekarz nie istnieje.
     * @throws PatientNotFoundException jeśli pacjent nie istnieje.
     * @throws ConsultingRoomNotFoundException jeśli gabinet nie istnieje.
     * @throws VisitAlreadyExistsException jeśli wykryto konflikt terminów dla lekarza, pacjenta lub gabinetu.
     * @throws VisitNotInScheduleException jeśli lekarz nie ma zdefiniowanego dyżuru w wybranym terminie i gabinecie.
     */
    public void addVisit(@Valid VisitRequest visitDataRequest) {
        if(!doctorRepository.existsById(visitDataRequest.doctorId())){
            throw new DoctorNotFoundException("Doctor with id " + visitDataRequest.doctorId() + " not found.");
        }
        if(!patientRepository.existsById(visitDataRequest.patientId())){
            throw new PatientNotFoundException("Patient with id " + visitDataRequest.patientId() + " not found.");
        }
        if(!consultingRoomRepository.existsById(visitDataRequest.consultingRoomId())){
            throw new ConsultingRoomNotFoundException("Consulting room with id " + visitDataRequest.consultingRoomId() + " not found.");
        }
        if(visitRepository.visitAlreadyExistsForDoctor(visitDataRequest.doctorId(), visitDataRequest.visitStart(), visitDataRequest.visitEnd())){
            throw new VisitAlreadyExistsException("Colliding visit exists for doctor with id " + visitDataRequest.doctorId() + " in the given time range.");
        }
        if(visitRepository.visitAlreadyExistsForConsultingRoom(visitDataRequest.consultingRoomId(), visitDataRequest.visitStart(), visitDataRequest.visitEnd())){
            throw new VisitAlreadyExistsException("Colliding visit exists for consulting room with id " + visitDataRequest.consultingRoomId() + " in the given time range.");
        }
        if(visitRepository.visitAlreadyExistsForPatient(visitDataRequest.patientId(), visitDataRequest.visitStart(), visitDataRequest.visitEnd())){
            throw new VisitAlreadyExistsException("Colliding visit exists for patient with id " + visitDataRequest.patientId() + " in the given time range.");
        }
        if(!scheduleRepository.ScheduleExistsForDoctorInPeriodInRoom(visitDataRequest.doctorId(),visitDataRequest.consultingRoomId(), visitDataRequest.visitStart(), visitDataRequest.visitEnd())){
            throw new VisitNotInScheduleException("No schedule for doctor with id " + visitDataRequest.doctorId() + " in the given time range.");
        }

        Doctor doctor = doctorRepository.findById(visitDataRequest.doctorId()).get();
        ConsultingRoom consultingRoom = consultingRoomRepository.findById(visitDataRequest.consultingRoomId()).get();
        Patient patient = patientRepository.findById(visitDataRequest.patientId()).get();
        visitRepository.save(visitDataRequest.toEntity(doctor, patient, consultingRoom));
    }


    /**
     * Metoda pomocnicza dzieląca czas dyżuru lekarza na pojedyncze sloty wizyt.
     *
     * @param schedule Szczegóły dyżuru (harmonogramu).
     * @param duration Czas trwania pojedynczej wizyty w minutach.
     * @return Lista potencjalnych terminów wizyt w ramach danego dyżuru.
     */
    private List<AvailabilityResponse> getAllPossibleVisitsForSchedule(ScheduleDetail schedule, int duration) {
        List<AvailabilityResponse> possibleVisits = new ArrayList<>();

        LocalDateTime currentSlotStart = schedule.getShiftStart();
        LocalDateTime shiftEnd = schedule.getShiftEnd();

        while (!currentSlotStart.plusMinutes(duration).isAfter(shiftEnd)) {
            LocalDateTime currentSlotEnd = currentSlotStart.plusMinutes(duration);
            possibleVisits.add(createAvailabilityResponse(schedule, currentSlotStart, currentSlotEnd));
            currentSlotStart = currentSlotEnd;
        }

        return possibleVisits;
    }

    /**
     * Metoda pomocnicza tworząca obiekt odpowiedzi z dostępnym terminem.
     *
     * @param schedule Dyżur, z którego pochodzi termin.
     * @param visitStart Godzina rozpoczęcia wizyty.
     * @param visitEnd Godzina zakończenia wizyty.
     * @return Obiekt {@link AvailabilityResponse} z danymi lekarza, gabinetu i czasem.
     */
    private AvailabilityResponse createAvailabilityResponse(ScheduleDetail schedule, LocalDateTime visitStart, LocalDateTime visitEnd){
        return new AvailabilityResponse(
                new DoctorBriefResponse(
                        schedule.getDoctor().getId(),
                        schedule.getDoctor().getFirstName(),
                        schedule.getDoctor().getLastName(),
                        schedule.getDoctor().getSpecialization().name()
                ),
                new ConsultingRoomBriefResponse(
                        schedule.getConsultingRoom().getId(),
                        schedule.getConsultingRoom().getRoomNumber(),
                        schedule.getConsultingRoom().getMedicalFacilities().isHasExaminationBed(),
                        schedule.getConsultingRoom().getMedicalFacilities().isHasECGMachine(),
                        schedule.getConsultingRoom().getMedicalFacilities().isHasScale(),
                        schedule.getConsultingRoom().getMedicalFacilities().isHasThermometer(),
                        schedule.getConsultingRoom().getMedicalFacilities().isHasDiagnosticSet()
                ),
                visitStart,
                visitEnd
        );
    }

}