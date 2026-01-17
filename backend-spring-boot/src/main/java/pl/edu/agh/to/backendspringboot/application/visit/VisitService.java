package pl.edu.agh.to.backendspringboot.application.visit;

import io.reactivex.rxjava3.core.Observable;
import jakarta.validation.Valid;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
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

import javax.print.Doc;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class VisitService {
    private final VisitRepository visitRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ConsultingRoomRepository consultingRoomRepository;

    public VisitService(VisitRepository visitRepository, ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, ConsultingRoomRepository consultingRoomRepository) {
        this.visitRepository = visitRepository;
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.consultingRoomRepository = consultingRoomRepository;
    }


    public Observable<AvailabilityResponse> getPossibleVisits(MedicalSpecialization specialization) {
        int duration= specialization.getVisitTime();
        return Observable.fromIterable(
                doctorRepository.findAllBySpecialization(specialization)
        ).subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
        .map(doctor -> scheduleRepository.findAllByDoctorIdDetail(doctor.getId()))
                .flatMapIterable(schedules -> schedules)
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.computation())
                .map(schedule -> getAllPossibleVisitsForSchedule(schedule, duration))
                .flatMapIterable(possibleVisits -> possibleVisits)
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

    public List<VisitBrief> getAllVisits(){
        return visitRepository.findAllVisits();
    }

    public VisitDetail getVisitById(int id){
        return visitRepository.findById(id);
    }

    public void deleteVisitById(int id) throws VisitNotFoundException {
        if(!visitRepository.existsById(id)){
            throw new VisitNotFoundException("Visit with id " + id + " not found.");
        }
        visitRepository.deleteById(id);
    }

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


    private List<AvailabilityResponse> getAllPossibleVisitsForSchedule(ScheduleDetail schedule, int duration){
        List<AvailabilityResponse> possibleVisits = new ArrayList<AvailabilityResponse>();
        int start = schedule.getShiftStart().getHour()*60 + schedule.getShiftStart().getMinute();
        int end = schedule.getShiftEnd().getHour()*60 + schedule.getShiftEnd().getMinute();
        for(int time = start; time + duration -1  <= end; time += duration + 1 ){
            int hour = time / 60;
            int minute = time % 60;
            LocalTime visitStart = LocalTime.of(hour, minute);
            LocalTime visitEnd = visitStart.plusMinutes(duration-1);
            possibleVisits.add(createAvailabilityResponse(schedule, visitStart, visitEnd));
        }
        return possibleVisits;
    }

    private AvailabilityResponse createAvailabilityResponse(ScheduleDetail schedule, LocalTime visitStart, LocalTime visitEnd){
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
