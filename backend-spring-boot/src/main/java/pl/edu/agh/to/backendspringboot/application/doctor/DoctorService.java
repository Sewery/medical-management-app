package pl.edu.agh.to.backendspringboot.application.doctor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorAssignedToScheduleException;
import pl.edu.agh.to.backendspringboot.domain.schedule.model.ScheduleBrief;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
import pl.edu.agh.to.backendspringboot.infrastructure.schedule.ScheduleRepository;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorBriefResponse;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorDetailResponse;
import pl.edu.agh.to.backendspringboot.presentation.doctor.dto.DoctorRequest;
import pl.edu.agh.to.backendspringboot.domain.doctor.exception.DoctorNotFoundException;

import java.util.List;

/**
 * Serwis realizujący logikę biznesową związaną z zarządzaniem lekarzami.
 * Odpowiada za komunikację między kontrolerem a warstwą danych (repozytorium),
 * a także za mapowanie obiektów domenowych na DTO.
 */
@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    /**
     * Konstruktor serwisu wstrzykujący zależność repozytorium.
     *
     * @param doctorRepository Repozytorium umożliwiające operacje na bazie danych lekarzy.
     */
    public DoctorService(DoctorRepository doctorRepository, ScheduleRepository scheduleRepository) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Tworzy i zapisuje nowego lekarza w systemie.
     * Metoda konwertuje obiekt żądania (DTO) na encję domenową przed zapisem.
     *
     * @param doctorRequest Obiekt zawierający dane niezbędne do utworzenia lekarza.
     */
    public void addDoctor(DoctorRequest doctorRequest){
        doctorRepository.save(DoctorRequest.toEntity(doctorRequest));
    }

    /**
     * Pobiera listę wszystkich lekarzy w formacie skróconym.
     * Dane są pobierane z repozytorium, a następnie mapowane na obiekty {@link DoctorBriefResponse}.
     *
     * @return Lista skróconych informacji o lekarzach.
     */
    public List<DoctorBriefResponse> getDoctors() {
        return doctorRepository.findDoctorsBrief().stream().map(DoctorBriefResponse::from).toList();
    }

    /**
     * Pobiera szczegółowe informacje o lekarzu na podstawie jego identyfikatora.
     *
     * @param id Unikalny identyfikator lekarza.
     * @return Obiekt {@link DoctorDetailResponse} zawierający pełne dane lekarza.
     * @throws DoctorNotFoundException jeśli lekarz o podanym identyfikatorze nie zostanie znaleziony.
     */
    public DoctorDetailResponse getDoctorInfoById(Integer id) {
        var doctorDetail = doctorRepository.findDoctorInfoById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id " + id + " not found"));

        List<ScheduleBrief> schedules = scheduleRepository.findAllByDoctorId(id);

        return DoctorDetailResponse.from(doctorDetail, schedules);
    }

    /**
     * Usuwa lekarza z systemu.
     * <p>
     * Przed usunięciem sprawdza, czy lekarz nie ma przypisanych przyszłych lub przeszłych dyżurów.
     *
     * @param id ID lekarza.
     * @throws DoctorNotFoundException           jeśli lekarz nie istnieje.
     * @throws DoctorAssignedToScheduleException jeśli lekarz ma przypisane dyżury (nie można usunąć).
     */
    @Transactional
    public void deleteDoctorById(Integer id) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Doctor with id " + id + " not found");
        }

        if (scheduleRepository.existsByDoctorId(id)) {
            throw new DoctorAssignedToScheduleException(
                    "Cannot delete doctor with id " + id + " because they have assigned schedules."
            );
        }

        doctorRepository.deleteById(id);
    }
}
