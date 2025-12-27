package pl.edu.agh.to.backendspringboot.application.doctor;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.backendspringboot.infrastructure.doctor.DoctorRepository;
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

    /**
     * Konstruktor serwisu wstrzykujący zależność repozytorium.
     *
     * @param doctorRepository Repozytorium umożliwiające operacje na bazie danych lekarzy.
     */
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
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
        return doctorRepository.findDoctorInfoById(id).map(DoctorDetailResponse::from)
                .orElseThrow(()->new DoctorNotFoundException("Doctor with id "+id+" not found"));
    }

    /**
     * Usuwa lekarza z systemu na podstawie jego identyfikatora.
     * Przed usunięciem następuje weryfikacja, czy lekarz o danym ID istnieje.
     *
     * @param id Unikalny identyfikator lekarza do usunięcia.
     * @throws DoctorNotFoundException jeśli lekarz o podanym identyfikatorze nie istnieje.
     */
    public void deleteDoctorById(Integer id) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Doctor with id " + id + " not found");
        }
        doctorRepository.deleteById(id);
    }
}
