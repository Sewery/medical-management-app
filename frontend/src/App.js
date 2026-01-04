import React, { useState, useEffect } from 'react';

// Ikony
const UserIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>;
const PatientIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>;
const TrashIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>;
const EyeIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>;
const PlusIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>;
const CloseIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>;
const StethoscopeIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4.8 2.3A.3.3 0 1 0 5 2H4a2 2 0 0 0-2 2v5a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6V4a2 2 0 0 0-2-2h-1a.2.2 0 1 0 .3.3"/><path d="M8 15v1a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6v-4"/><circle cx="20" cy="10" r="2"/></svg>;
const RoomIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>;
const CalendarIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>;
const CheckIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"/></svg>;
const XMarkIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>;
const ClockIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>;

// Lista specjalizacji zgodna z Twoim Java Enum
const SPECIALIZATIONS = [
 { value: "INTERNAL_MEDICINE", label: "Choroby wewnętrzne" },
 { value: "FAMILY_MEDICINE", label: "Medycyna rodzinna" },
 { value: "PEDIATRICS", label: "Pediatria" },
 { value: "ALLERGOLOGY", label: "Alergologia" },
 { value: "ANESTHESIOLOGY", label: "Anestezjologia" },
 { value: "CARDIOLOGY", label: "Kardiologia" },
 { value: "DERMATOLOGY", label: "Dermatologia" },
 { value: "ENDOCRINOLOGY", label: "Endokrynologia" },
 { value: "GASTROENTEROLOGY", label: "Gastroenterologia" },
 { value: "GENERAL_SURGERY", label: "Chirurgia ogólna" },
 { value: "GYNECOLOGY", label: "Ginekologia" },
 { value: "NEUROLOGY", label: "Neurologia" },
 { value: "ONCOLOGY", label: "Onkologia" },
 { value: "OPHTHALMOLOGY", label: "Okulistyka" },
 { value: "ORTHOPEDICS", label: "Ortopedia" },
 { value: "OTOLARYNGOLOGY", label: "Otolaryngologia" },
 { value: "PSYCHIATRY", label: "Psychiatria" },
 { value: "PULMONOLOGY", label: "Pulmonologia" },
 { value: "RADIOLOGY", label: "Radiologia" },
 { value: "RHEUMATOLOGY", label: "Reumatologia" },
 { value: "UROLOGY", label: "Urologia" },
 { value: "DENTISTRY", label: "Stomatologia" },
];

function App() {
  // Aktywna zakładka
  const [activeTab, setActiveTab] = useState('doctors');
  
  // Stan dla lekarzy
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    pesel: '',
    specialization: 'INTERNAL_MEDICINE',
    street: '',
    city: '',
    postalCode: ''
  });

  // Stan dla pacjentów
  const [patients, setPatients] = useState([]);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [isPatientFormOpen, setIsPatientFormOpen] = useState(false);
  const [patientError, setPatientError] = useState('');
  
  const [patientFormData, setPatientFormData] = useState({
    firstName: '',
    lastName: '',
    pesel: '',
    street: '',
    city: '',
    postalCode: ''
  });

  // Stan dla gabinetów
  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [isRoomFormOpen, setIsRoomFormOpen] = useState(false);
  const [roomError, setRoomError] = useState('');
  
  const [roomFormData, setRoomFormData] = useState({
    roomNumber: '',
    hasExaminationBed: false,
    hasECGMachine: false,
    hasScale: false,
    hasThermometer: false,
    hasDiagnosticSet: false
  });

  // Stan dla dyżurów
  const [scheduleError, setScheduleError] = useState('');
  const [scheduleSuccess, setScheduleSuccess] = useState('');
  const [availability, setAvailability] = useState(null);
  const [isCheckingAvailability, setIsCheckingAvailability] = useState(false);
  
  const [scheduleFormData, setScheduleFormData] = useState({
    startTime: '',
    endTime: '',
    doctorId: '',
    consultingRoomId: ''
  });

  const API_URL = 'http://localhost:8080/doctors';
  const PATIENTS_API_URL = 'http://localhost:8080/patients';
  const ROOMS_API_URL = 'http://localhost:8080/consulting-room';
  const SCHEDULES_API_URL = 'http://localhost:8080/schedules';

  useEffect(() => {
    fetchDoctors();
    fetchPatients();
    fetchRooms();
  }, []);

  const fetchDoctors = async () => {
    try {
      const res = await fetch(API_URL);
      const data = await res.json();
      setDoctors(data);
    } catch (err) {
      console.error("Błąd sieci", err);
    }
  };

  const fetchPatients = async () => {
    try {
      const res = await fetch(PATIENTS_API_URL);
      const data = await res.json();
      setPatients(data);
    } catch (err) {
      console.error("Błąd sieci - pacjenci", err);
    }
  };

  const fetchRooms = async () => {
    try {
      const res = await fetch(ROOMS_API_URL);
      const data = await res.json();
      setRooms(data);
    } catch (err) {
      console.error("Błąd sieci - gabinety", err);
    }
  };

  const fetchRoomDetails = async (id) => {
    if (!id) {
        alert("Błąd: Brak ID gabinetu.");
        return;
    }
    try {
      const res = await fetch(`${ROOMS_API_URL}/${id}`);
      if (res.ok) {
        const details = await res.json();
        setSelectedRoom(details);
      } else {
        try {
          const errData = await res.json();
          alert(errData.message || "Nie udało się pobrać szczegółów gabinetu.");
        } catch {
          alert("Nie udało się pobrać szczegółów gabinetu (404 Not Found).");
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchDetails = async (id) => {
    if (!id) {
        alert("Błąd: Brak ID lekarza. Zrestartuj Backend po dodaniu pola 'id' w DoctorBriefResponse.");
        return;
    }
    try {
      const res = await fetch(`${API_URL}/${id}`);
      if (res.ok) {
        const details = await res.json();
        setSelectedDoctor(details);
      } else {
        try {
          const errData = await res.json();
          alert(errData.message || "Nie udało się pobrać szczegółów lekarza.");
        } catch {
          alert("Nie udało się pobrać szczegółów (404 Not Found).");
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchPatientDetails = async (id) => {
    if (!id) {
        alert("Błąd: Brak ID pacjenta.");
        return;
    }
    try {
      const res = await fetch(`${PATIENTS_API_URL}/${id}`);
      if (res.ok) {
        const details = await res.json();
        setSelectedPatient(details);
      } else {
        try {
          const errData = await res.json();
          alert(errData.message || "Nie udało się pobrać szczegółów pacjenta.");
        } catch {
          alert("Nie udało się pobrać szczegółów pacjenta (404 Not Found).");
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      const res = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (res.ok) {
        fetchDoctors();
        setIsFormOpen(false);
        setFormData({ 
            firstName: '', lastName: '', pesel: '', 
            specialization: 'INTERNAL_MEDICINE', street: '', city: '', postalCode: '' 
        });
      } else {
        const errData = await res.json();
        setError(errData.message || 'Błąd walidacji danych');
      }
    } catch (err) {
      setError('Błąd połączenia z serwerem');
    }
  };

  const handlePatientSubmit = async (e) => {
    e.preventDefault();
    setPatientError('');
    
    try {
      const res = await fetch(PATIENTS_API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(patientFormData)
      });

      if (res.ok) {
        fetchPatients();
        setIsPatientFormOpen(false);
        setPatientFormData({ 
            firstName: '', lastName: '', pesel: '', 
            street: '', city: '', postalCode: '' 
        });
      } else {
        const errData = await res.json();
        setPatientError(errData.message || 'Błąd walidacji danych');
      }
    } catch (err) {
      setPatientError('Błąd połączenia z serwerem');
    }
  };

  const handleDelete = async (id) => {
    if (!id) {
        console.error("Próba usunięcia lekarza z ID: undefined");
        alert("Błąd: Frontend nie widzi ID lekarza. Upewnij się, że zaktualizowałeś plik Java (DoctorBriefResponse).");
        return;
    }

    if (!window.confirm("Czy na pewno chcesz usunąć tego lekarza?")) return;

    try {
      const res = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
      if (res.ok) {
        fetchDoctors();
      } else {
        try {
          const errData = await res.json();
          alert(errData.message || "Błąd usuwania lekarza.");
        } catch {
          alert("Błąd usuwania. Sprawdź czy masz endpoint DELETE w kontrolerze.");
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handlePatientDelete = async (id) => {
    if (!id) {
        console.error("Próba usunięcia pacjenta z ID: undefined");
        alert("Błąd: Frontend nie widzi ID pacjenta.");
        return;
    }

    if (!window.confirm("Czy na pewno chcesz usunąć tego pacjenta?")) return;

    try {
      const res = await fetch(`${PATIENTS_API_URL}/${id}`, { method: 'DELETE' });
      if (res.ok) {
        fetchPatients();
      } else {
        try {
          const errData = await res.json();
          alert(errData.message || "Błąd usuwania pacjenta.");
        } catch {
          alert("Błąd usuwania pacjenta.");
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleRoomSubmit = async (e) => {
    e.preventDefault();
    setRoomError('');
    
    try {
      const res = await fetch(ROOMS_API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(roomFormData)
      });

      if (res.ok) {
        fetchRooms();
        setIsRoomFormOpen(false);
        setRoomFormData({ 
            roomNumber: '',
            hasExaminationBed: false,
            hasECGMachine: false,
            hasScale: false,
            hasThermometer: false,
            hasDiagnosticSet: false
        });
      } else {
        const errData = await res.json();
        setRoomError(errData.message || 'Błąd walidacji danych');
      }
    } catch (err) {
      setRoomError('Błąd połączenia z serwerem');
    }
  };

  const handleRoomDelete = async (id) => {
    if (!id) {
        console.error("Próba usunięcia gabinetu z ID: undefined");
        alert("Błąd: Frontend nie widzi ID gabinetu.");
        return;
    }

    if (!window.confirm("Czy na pewno chcesz usunąć ten gabinet?")) return;

    try {
      const res = await fetch(`${ROOMS_API_URL}/${id}`, { method: 'DELETE' });
      if (res.ok) {
        fetchRooms();
      } else {
        try {
          const errData = await res.json();
          alert(errData.message || "Błąd usuwania gabinetu.");
        } catch {
          alert("Błąd usuwania gabinetu.");
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleRoomChange = (e) => {
    const { name, value, type, checked } = e.target;
    setRoomFormData({ 
      ...roomFormData, 
      [name]: type === 'checkbox' ? checked : value 
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Specjalna obsługa PESEL (tylko cyfry, max 11)
    if (name === 'pesel') {
        const onlyNums = value.replace(/[^0-9]/g, ''); // Usuń wszystko co nie jest cyfrą
        if (onlyNums.length <= 11) {
            setFormData({ ...formData, [name]: onlyNums });
        }
    } else {
        setFormData({ ...formData, [name]: value });
    }
  };

  const handlePatientChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'pesel') {
        const onlyNums = value.replace(/[^0-9]/g, '');
        if (onlyNums.length <= 11) {
            setPatientFormData({ ...patientFormData, [name]: onlyNums });
        }
    } else {
        setPatientFormData({ ...patientFormData, [name]: value });
    }
  };

  const handleScheduleChange = (e) => {
    const { name, value } = e.target;
    setScheduleFormData({ ...scheduleFormData, [name]: value });
  };

  const checkAvailability = async () => {
    if (!scheduleFormData.startTime || !scheduleFormData.endTime) {
      setScheduleError('Podaj godzinę rozpoczęcia i zakończenia');
      return;
    }
    
    setScheduleError('');
    setScheduleSuccess('');
    setIsCheckingAvailability(true);
    setAvailability(null);
    
    try {
      const res = await fetch(
        `${SCHEDULES_API_URL}/availability?startTime=${scheduleFormData.startTime}&endTime=${scheduleFormData.endTime}`
      );
      
      if (res.ok) {
        const data = await res.json();
        setAvailability(data);
        setScheduleFormData({ ...scheduleFormData, doctorId: '', consultingRoomId: '' });
      } else {
        try {
          const errData = await res.json();
          setScheduleError(errData.message || 'Błąd sprawdzania dostępności');
        } catch {
          setScheduleError('Błąd sprawdzania dostępności');
        }
      }
    } catch (err) {
      setScheduleError('Błąd połączenia z serwerem');
      console.error(err);
    } finally {
      setIsCheckingAvailability(false);
    }
  };

  const handleScheduleSubmit = async (e) => {
    e.preventDefault();
    setScheduleError('');
    setScheduleSuccess('');
    
    if (!scheduleFormData.doctorId || !scheduleFormData.consultingRoomId) {
      setScheduleError('Wybierz lekarza i gabinet');
      return;
    }
    
    try {
      const res = await fetch(SCHEDULES_API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          startTime: scheduleFormData.startTime,
          endTime: scheduleFormData.endTime,
          doctorId: parseInt(scheduleFormData.doctorId),
          consultingRoomId: parseInt(scheduleFormData.consultingRoomId)
        })
      });

      if (res.ok) {
        setScheduleSuccess('Dyżur został zaplanowany!');
        setScheduleFormData({ startTime: '', endTime: '', doctorId: '', consultingRoomId: '' });
        setAvailability(null);
        // Odśwież dane gabinetów żeby pokazać nowy dyżur
        fetchRooms();
      } else {
        try {
          const errData = await res.json();
          setScheduleError(errData.message || 'Błąd planowania dyżuru');
        } catch {
          setScheduleError('Błąd planowania dyżuru');
        }
      }
    } catch (err) {
      setScheduleError('Błąd połączenia z serwerem');
      console.error(err);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 p-6 font-sans">
      <div className="max-w-5xl mx-auto">
        
        {/* NAGŁÓWEK */}
        <header className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">System Medyczny</h1>
            <p className="text-slate-500 mt-1">Zarządzanie personelem, pacjentami i gabinetami</p>
          </div>
        </header>

        {/* ZAKŁADKI */}
        <div className="flex gap-2 mb-6 flex-wrap">
          <button
            onClick={() => setActiveTab('doctors')}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg font-medium transition ${
              activeTab === 'doctors'
                ? 'bg-blue-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <StethoscopeIcon /> Lekarze
          </button>
          <button
            onClick={() => setActiveTab('patients')}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg font-medium transition ${
              activeTab === 'patients'
                ? 'bg-emerald-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <PatientIcon /> Pacjenci
          </button>
          <button
            onClick={() => setActiveTab('rooms')}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg font-medium transition ${
              activeTab === 'rooms'
                ? 'bg-purple-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <RoomIcon /> Gabinety
          </button>
          <button
            onClick={() => setActiveTab('schedules')}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg font-medium transition ${
              activeTab === 'schedules'
                ? 'bg-orange-600 text-white shadow-sm'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <CalendarIcon /> Dyżury
          </button>
        </div>

        {/* ================== SEKCJA LEKARZY ================== */}
        {activeTab === 'doctors' && (
          <>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold text-slate-800">Baza Lekarzy</h2>
              <button 
                onClick={() => setIsFormOpen(!isFormOpen)}
                className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-lg font-medium transition shadow-sm"
              >
                {isFormOpen ? <CloseIcon /> : <PlusIcon />}
                {isFormOpen ? 'Zamknij formularz' : 'Dodaj lekarza'}
              </button>
            </div>

        {/* FORMULARZ */}
        {isFormOpen && (
          <div className="bg-white p-6 rounded-xl shadow-md border border-slate-200 mb-8 animate-fade-in-down">
            <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
              <UserIcon /> Nowy Lekarz
            </h2>
            
            {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm border border-red-100">{error}</div>}

            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <input name="firstName" placeholder="Imię" value={formData.firstName} onChange={handleChange} className="input-field" required />
              <input name="lastName" placeholder="Nazwisko" value={formData.lastName} onChange={handleChange} className="input-field" required />
              
              {/* PESEL Z BLOKADĄ */}
              <input 
                name="pesel" 
                placeholder="PESEL (11 cyfr)" 
                value={formData.pesel} 
                onChange={handleChange} 
                className="input-field" 
                required 
                maxLength={11} // HTML limit
              />
              
              {/* SELECT SPECJALIZACJA */}
              <select 
                name="specialization" 
                value={formData.specialization} 
                onChange={handleChange} 
                className="input-field bg-white" 
                required
              >
                {SPECIALIZATIONS.map(spec => (
                    <option key={spec.value} value={spec.value}>
                        {spec.label}
                    </option>
                ))}
              </select>
              
              <div className="md:col-span-2 mt-2 border-t pt-4">
                <p className="text-sm font-bold text-slate-400 uppercase tracking-wide mb-3">Adres</p>
              </div>
              
              <input name="street" placeholder="Ulica i numer" value={formData.street} onChange={handleChange} className="input-field" required />
              <input name="postalCode" placeholder="Kod pocztowy (XX-XXX)" value={formData.postalCode} onChange={handleChange} className="input-field" required />
              <input name="city" placeholder="Miasto" value={formData.city} onChange={handleChange} className="input-field md:col-span-2" required />

              <div className="md:col-span-2 mt-4 flex justify-end">
                <button type="submit" className="bg-emerald-600 hover:bg-emerald-700 text-white px-8 py-2.5 rounded-lg font-medium shadow-sm transition">
                  Zapisz dane
                </button>
              </div>
            </form>
          </div>
        )}

        {/* LISTA LEKARZY */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold">
              <tr>
                <th className="px-6 py-4">Lekarz</th>
                <th className="px-6 py-4">Specjalizacja</th>
                <th className="px-6 py-4 text-right">Akcje</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {doctors.length === 0 ? (
                <tr>
                  <td colSpan="3" className="px-6 py-8 text-center text-slate-400">Brak lekarzy w systemie.</td>
                </tr>
              ) : (
                doctors.map((doc) => (
                  <tr key={doc.id || Math.random()} className="hover:bg-slate-50 transition">
                    <td className="px-6 py-4 font-medium text-slate-900">
                      {doc.firstName} {doc.lastName}
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        {doc.specialization}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right space-x-2">
                      <button 
                        onClick={() => fetchDetails(doc.id)}
                        className="text-slate-400 hover:text-blue-600 transition p-1"
                        title="Szczegóły"
                      >
                        <EyeIcon />
                      </button>
                      <button 
                        onClick={() => handleDelete(doc.id)}
                        className="text-slate-400 hover:text-red-600 transition p-1"
                        title="Usuń"
                      >
                        <TrashIcon />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
          </>
        )}

        {/* ================== SEKCJA PACJENTÓW ================== */}
        {activeTab === 'patients' && (
          <>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold text-slate-800">Baza Pacjentów</h2>
              <button 
                onClick={() => setIsPatientFormOpen(!isPatientFormOpen)}
                className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white px-5 py-2.5 rounded-lg font-medium transition shadow-sm"
              >
                {isPatientFormOpen ? <CloseIcon /> : <PlusIcon />}
                {isPatientFormOpen ? 'Zamknij formularz' : 'Dodaj pacjenta'}
              </button>
            </div>

            {/* FORMULARZ PACJENTA */}
            {isPatientFormOpen && (
              <div className="bg-white p-6 rounded-xl shadow-md border border-slate-200 mb-8 animate-fade-in-down">
                <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                  <PatientIcon /> Nowy Pacjent
                </h2>
                
                {patientError && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm border border-red-100">{patientError}</div>}

                <form onSubmit={handlePatientSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <input name="firstName" placeholder="Imię" value={patientFormData.firstName} onChange={handlePatientChange} className="input-field" required />
                  <input name="lastName" placeholder="Nazwisko" value={patientFormData.lastName} onChange={handlePatientChange} className="input-field" required />
                  
                  <input 
                    name="pesel" 
                    placeholder="PESEL (11 cyfr)" 
                    value={patientFormData.pesel} 
                    onChange={handlePatientChange} 
                    className="input-field md:col-span-2" 
                    required 
                    maxLength={11}
                  />
                  
                  <div className="md:col-span-2 mt-2 border-t pt-4">
                    <p className="text-sm font-bold text-slate-400 uppercase tracking-wide mb-3">Adres</p>
                  </div>
                  
                  <input name="street" placeholder="Ulica i numer" value={patientFormData.street} onChange={handlePatientChange} className="input-field" required />
                  <input name="postalCode" placeholder="Kod pocztowy (XX-XXX)" value={patientFormData.postalCode} onChange={handlePatientChange} className="input-field" required />
                  <input name="city" placeholder="Miasto" value={patientFormData.city} onChange={handlePatientChange} className="input-field md:col-span-2" required />

                  <div className="md:col-span-2 mt-4 flex justify-end">
                    <button type="submit" className="bg-emerald-600 hover:bg-emerald-700 text-white px-8 py-2.5 rounded-lg font-medium shadow-sm transition">
                      Zapisz dane
                    </button>
                  </div>
                </form>
              </div>
            )}

            {/* LISTA PACJENTÓW */}
            <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
              <table className="w-full text-left">
                <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold">
                  <tr>
                    <th className="px-6 py-4">Pacjent</th>
                    <th className="px-6 py-4">PESEL</th>
                    <th className="px-6 py-4 text-right">Akcje</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {patients.length === 0 ? (
                    <tr>
                      <td colSpan="3" className="px-6 py-8 text-center text-slate-400">Brak pacjentów w systemie.</td>
                    </tr>
                  ) : (
                    patients.map((patient) => (
                      <tr key={patient.id || Math.random()} className="hover:bg-slate-50 transition">
                        <td className="px-6 py-4 font-medium text-slate-900">
                          {patient.firstName} {patient.lastName}
                        </td>
                        <td className="px-6 py-4">
                          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800 font-mono">
                            {patient.pesel}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-right space-x-2">
                          <button 
                            onClick={() => fetchPatientDetails(patient.id)}
                            className="text-slate-400 hover:text-emerald-600 transition p-1"
                            title="Szczegóły"
                          >
                            <EyeIcon />
                          </button>
                          <button 
                            onClick={() => handlePatientDelete(patient.id)}
                            className="text-slate-400 hover:text-red-600 transition p-1"
                            title="Usuń"
                          >
                            <TrashIcon />
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}

        {/* ================== SEKCJA GABINETÓW ================== */}
        {activeTab === 'rooms' && (
          <>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold text-slate-800">Gabinety Lekarskie</h2>
              <button 
                onClick={() => setIsRoomFormOpen(!isRoomFormOpen)}
                className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 text-white px-5 py-2.5 rounded-lg font-medium transition shadow-sm"
              >
                {isRoomFormOpen ? <CloseIcon /> : <PlusIcon />}
                {isRoomFormOpen ? 'Zamknij formularz' : 'Dodaj gabinet'}
              </button>
            </div>

            {/* FORMULARZ GABINETU */}
            {isRoomFormOpen && (
              <div className="bg-white p-6 rounded-xl shadow-md border border-slate-200 mb-8 animate-fade-in-down">
                <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                  <RoomIcon /> Nowy Gabinet
                </h2>
                
                {roomError && <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm border border-red-100">{roomError}</div>}

                <form onSubmit={handleRoomSubmit} className="space-y-4">
                  <div>
                    <input 
                      name="roomNumber" 
                      placeholder="Numer pokoju (np. 101, A12)" 
                      value={roomFormData.roomNumber} 
                      onChange={handleRoomChange} 
                      className="input-field" 
                      required 
                    />
                  </div>
                  
                  <div className="border-t pt-4">
                    <p className="text-sm font-bold text-slate-400 uppercase tracking-wide mb-3">Wyposażenie</p>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg cursor-pointer hover:bg-slate-100 transition">
                        <input 
                          type="checkbox" 
                          name="hasExaminationBed" 
                          checked={roomFormData.hasExaminationBed} 
                          onChange={handleRoomChange}
                          className="w-5 h-5 rounded border-slate-300 text-purple-600 focus:ring-purple-500"
                        />
                        <span className="text-slate-700">Łóżko do badań</span>
                      </label>
                      <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg cursor-pointer hover:bg-slate-100 transition">
                        <input 
                          type="checkbox" 
                          name="hasECGMachine" 
                          checked={roomFormData.hasECGMachine} 
                          onChange={handleRoomChange}
                          className="w-5 h-5 rounded border-slate-300 text-purple-600 focus:ring-purple-500"
                        />
                        <span className="text-slate-700">Aparat EKG</span>
                      </label>
                      <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg cursor-pointer hover:bg-slate-100 transition">
                        <input 
                          type="checkbox" 
                          name="hasScale" 
                          checked={roomFormData.hasScale} 
                          onChange={handleRoomChange}
                          className="w-5 h-5 rounded border-slate-300 text-purple-600 focus:ring-purple-500"
                        />
                        <span className="text-slate-700">Waga</span>
                      </label>
                      <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg cursor-pointer hover:bg-slate-100 transition">
                        <input 
                          type="checkbox" 
                          name="hasThermometer" 
                          checked={roomFormData.hasThermometer} 
                          onChange={handleRoomChange}
                          className="w-5 h-5 rounded border-slate-300 text-purple-600 focus:ring-purple-500"
                        />
                        <span className="text-slate-700">Termometr</span>
                      </label>
                      <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg cursor-pointer hover:bg-slate-100 transition md:col-span-2">
                        <input 
                          type="checkbox" 
                          name="hasDiagnosticSet" 
                          checked={roomFormData.hasDiagnosticSet} 
                          onChange={handleRoomChange}
                          className="w-5 h-5 rounded border-slate-300 text-purple-600 focus:ring-purple-500"
                        />
                        <span className="text-slate-700">Zestaw diagnostyczny</span>
                      </label>
                    </div>
                  </div>

                  <div className="flex justify-end pt-2">
                    <button type="submit" className="bg-purple-600 hover:bg-purple-700 text-white px-8 py-2.5 rounded-lg font-medium shadow-sm transition">
                      Zapisz gabinet
                    </button>
                  </div>
                </form>
              </div>
            )}

            {/* LISTA GABINETÓW */}
            <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
              <table className="w-full text-left">
                <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold">
                  <tr>
                    <th className="px-6 py-4">Numer</th>
                    <th className="px-6 py-4">Wyposażenie</th>
                    <th className="px-6 py-4 text-right">Akcje</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {rooms.length === 0 ? (
                    <tr>
                      <td colSpan="3" className="px-6 py-8 text-center text-slate-400">Brak gabinetów w systemie.</td>
                    </tr>
                  ) : (
                    rooms.map((room) => (
                      <tr key={room.id || Math.random()} className="hover:bg-slate-50 transition">
                        <td className="px-6 py-4 font-medium text-slate-900">
                          Pokój {room.roomNumber}
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex flex-wrap gap-1">
                            {room.hasExaminationBed && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-800">Łóżko</span>}
                            {room.hasECGMachine && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-800">EKG</span>}
                            {room.hasScale && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-800">Waga</span>}
                            {room.hasThermometer && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-800">Termometr</span>}
                            {room.hasDiagnosticSet && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-800">Diagnostyka</span>}
                            {!room.hasExaminationBed && !room.hasECGMachine && !room.hasScale && !room.hasThermometer && !room.hasDiagnosticSet && (
                              <span className="text-slate-400 text-xs">Brak wyposażenia</span>
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-4 text-right space-x-2">
                          <button 
                            onClick={() => fetchRoomDetails(room.id)}
                            className="text-slate-400 hover:text-purple-600 transition p-1"
                            title="Szczegóły"
                          >
                            <EyeIcon />
                          </button>
                          <button 
                            onClick={() => handleRoomDelete(room.id)}
                            className="text-slate-400 hover:text-red-600 transition p-1"
                            title="Usuń"
                          >
                            <TrashIcon />
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}

        {/* ================== SEKCJA DYŻURÓW ================== */}
        {activeTab === 'schedules' && (
          <>
            <div className="mb-4">
              <h2 className="text-xl font-semibold text-slate-800">Planowanie Dyżurów</h2>
              <p className="text-slate-500 text-sm mt-1">Sprawdź dostępność i zaplanuj dyżur lekarza w gabinecie</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-md border border-slate-200 mb-8">
              {scheduleError && (
                <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4 text-sm border border-red-100">
                  {scheduleError}
                </div>
              )}
              {scheduleSuccess && (
                <div className="bg-green-50 text-green-600 p-3 rounded-lg mb-4 text-sm border border-green-100">
                  {scheduleSuccess}
                </div>
              )}

              {/* KROK 1: Wybór godzin */}
              <div className="mb-6">
                <h3 className="text-lg font-semibold mb-3 flex items-center gap-2">
                  <ClockIcon /> Krok 1: Wybierz przedział czasowy
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-600 mb-1">Godzina rozpoczęcia</label>
                    <input 
                      type="time" 
                      name="startTime" 
                      value={scheduleFormData.startTime} 
                      onChange={handleScheduleChange}
                      className="input-field"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-600 mb-1">Godzina zakończenia</label>
                    <input 
                      type="time" 
                      name="endTime" 
                      value={scheduleFormData.endTime} 
                      onChange={handleScheduleChange}
                      className="input-field"
                    />
                  </div>
                  <div className="flex items-end">
                    <button 
                      onClick={checkAvailability}
                      disabled={isCheckingAvailability}
                      className="w-full bg-orange-600 hover:bg-orange-700 disabled:bg-orange-400 text-white px-5 py-2.5 rounded-lg font-medium transition shadow-sm"
                    >
                      {isCheckingAvailability ? 'Sprawdzam...' : 'Sprawdź dostępność'}
                    </button>
                  </div>
                </div>
              </div>

              {/* KROK 2: Wyniki dostępności */}
              {availability && (
                <div className="border-t pt-6">
                  <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                    <CalendarIcon /> Krok 2: Wybierz lekarza i gabinet
                  </h3>
                  
                  <form onSubmit={handleScheduleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Lista dostępnych lekarzy */}
                    <div>
                      <label className="block text-sm font-medium text-slate-600 mb-2">Dostępni lekarze</label>
                      {availability.doctors && availability.doctors.length > 0 ? (
                        <div className="space-y-2 max-h-60 overflow-y-auto">
                          {availability.doctors.map((doctor) => (
                            <label 
                              key={doctor.id} 
                              className={`flex items-center gap-3 p-3 rounded-lg cursor-pointer transition border ${
                                scheduleFormData.doctorId === String(doctor.id)
                                  ? 'bg-orange-50 border-orange-300'
                                  : 'bg-slate-50 border-transparent hover:bg-slate-100'
                              }`}
                            >
                              <input 
                                type="radio" 
                                name="doctorId" 
                                value={doctor.id}
                                checked={scheduleFormData.doctorId === String(doctor.id)}
                                onChange={handleScheduleChange}
                                className="w-4 h-4 text-orange-600 focus:ring-orange-500"
                              />
                              <div>
                                <p className="font-medium text-slate-800">{doctor.firstName} {doctor.lastName}</p>
                                <p className="text-xs text-slate-500">{doctor.specialization}</p>
                              </div>
                            </label>
                          ))}
                        </div>
                      ) : (
                        <p className="text-slate-400 text-sm p-3 bg-slate-50 rounded-lg">Brak dostępnych lekarzy w tym terminie</p>
                      )}
                    </div>

                    {/* Lista dostępnych gabinetów */}
                    <div>
                      <label className="block text-sm font-medium text-slate-600 mb-2">Dostępne gabinety</label>
                      {availability.consultingRooms && availability.consultingRooms.length > 0 ? (
                        <div className="space-y-2 max-h-60 overflow-y-auto">
                          {availability.consultingRooms.map((room) => (
                            <label 
                              key={room.id} 
                              className={`flex items-center gap-3 p-3 rounded-lg cursor-pointer transition border ${
                                scheduleFormData.consultingRoomId === String(room.id)
                                  ? 'bg-orange-50 border-orange-300'
                                  : 'bg-slate-50 border-transparent hover:bg-slate-100'
                              }`}
                            >
                              <input 
                                type="radio" 
                                name="consultingRoomId" 
                                value={room.id}
                                checked={scheduleFormData.consultingRoomId === String(room.id)}
                                onChange={handleScheduleChange}
                                className="w-4 h-4 text-orange-600 focus:ring-orange-500"
                              />
                              <div>
                                <p className="font-medium text-slate-800">Pokój {room.roomNumber}</p>
                                <div className="flex flex-wrap gap-1 mt-1">
                                  {room.hasExaminationBed && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Łóżko</span>}
                                  {room.hasECGMachine && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">EKG</span>}
                                  {room.hasScale && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Waga</span>}
                                  {room.hasThermometer && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Termometr</span>}
                                  {room.hasDiagnosticSet && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Diagnostyka</span>}
                                </div>
                              </div>
                            </label>
                          ))}
                        </div>
                      ) : (
                        <p className="text-slate-400 text-sm p-3 bg-slate-50 rounded-lg">Brak dostępnych gabinetów w tym terminie</p>
                      )}
                    </div>

                    {/* Przycisk zaplanuj */}
                    <div className="md:col-span-2 flex justify-end pt-4 border-t">
                      <button 
                        type="submit"
                        disabled={!scheduleFormData.doctorId || !scheduleFormData.consultingRoomId}
                        className="bg-green-600 hover:bg-green-700 disabled:bg-slate-300 disabled:cursor-not-allowed text-white px-8 py-2.5 rounded-lg font-medium shadow-sm transition"
                      >
                        Zaplanuj dyżur
                      </button>
                    </div>
                  </form>
                </div>
              )}
            </div>
          </>
        )}

      </div>

      {/* MODAL ZE SZCZEGÓŁAMI LEKARZA */}
      {selectedDoctor && (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center p-4 z-50 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-2xl max-w-lg w-full overflow-hidden animate-fade-in-up">
            <div className="bg-slate-50 px-6 py-4 border-b border-slate-200 flex justify-between items-center">
              <h3 className="font-bold text-lg text-slate-800">Karta Lekarza</h3>
              <button onClick={() => setSelectedDoctor(null)} className="text-slate-400 hover:text-slate-600">
                <CloseIcon />
              </button>
            </div>
            <div className="p-6 space-y-4 max-h-[70vh] overflow-y-auto">
              <div>
                <label className="text-xs text-slate-400 uppercase font-bold">Imię i Nazwisko</label>
                <p className="text-lg font-medium text-slate-900">{selectedDoctor.firstName} {selectedDoctor.lastName}</p>
              </div>
              <div>
                <label className="text-xs text-slate-400 uppercase font-bold">Specjalizacja</label>
                <p className="text-slate-700">{selectedDoctor.specialization}</p>
              </div>
              <div>
                <label className="text-xs text-slate-400 uppercase font-bold">PESEL</label>
                <p className="font-mono text-slate-600 bg-slate-100 inline-block px-2 py-0.5 rounded">{selectedDoctor.pesel}</p>
              </div>
              <div className="pt-2 border-t border-slate-100">
                <label className="text-xs text-slate-400 uppercase font-bold">Adres</label>
                <p className="text-slate-700">
                  ul. {selectedDoctor.street}<br/>
                  {selectedDoctor.postalCode} {selectedDoctor.city}
                </p>
              </div>
              
              {/* HARMONOGRAM DYŻURÓW */}
              {selectedDoctor.schedules && selectedDoctor.schedules.length > 0 && (
                <div className="pt-2 border-t border-slate-100">
                  <label className="text-xs text-slate-400 uppercase font-bold mb-3 block">Harmonogram Dyżurów</label>
                  <div className="space-y-2 max-h-48 overflow-y-auto">
                    {selectedDoctor.schedules.map((schedule, idx) => (
                      <div key={idx} className="bg-blue-50 p-3 rounded-lg">
                        <div className="flex justify-between items-start">
                          <div>
                            <p className="font-medium text-slate-800">
                              Pokój {schedule.consultingRoom?.roomNumber}
                            </p>
                            <div className="flex flex-wrap gap-1 mt-1">
                              {schedule.consultingRoom?.hasExaminationBed && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Łóżko</span>}
                              {schedule.consultingRoom?.hasECGMachine && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">EKG</span>}
                              {schedule.consultingRoom?.hasScale && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Waga</span>}
                              {schedule.consultingRoom?.hasThermometer && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Termometr</span>}
                              {schedule.consultingRoom?.hasDiagnosticSet && <span className="text-xs bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">Diagnostyka</span>}
                            </div>
                          </div>
                          <span className="text-sm font-medium text-blue-600">
                            {schedule.dutyTime?.shiftStart?.slice(0, 5)} - {schedule.dutyTime?.shiftEnd?.slice(0, 5)}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
              
              {(!selectedDoctor.schedules || selectedDoctor.schedules.length === 0) && (
                <div className="pt-2 border-t border-slate-100">
                  <label className="text-xs text-slate-400 uppercase font-bold mb-2 block">Harmonogram Dyżurów</label>
                  <p className="text-slate-400 text-sm">Brak zaplanowanych dyżurów</p>
                </div>
              )}
            </div>
            <div className="bg-slate-50 px-6 py-3 border-t border-slate-200 text-right">
              <button 
                onClick={() => setSelectedDoctor(null)}
                className="text-sm font-medium text-slate-600 hover:text-slate-900 px-4 py-2"
              >
                Zamknij
              </button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL ZE SZCZEGÓŁAMI PACJENTA */}
      {selectedPatient && (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center p-4 z-50 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-2xl max-w-md w-full overflow-hidden animate-fade-in-up">
            <div className="bg-emerald-50 px-6 py-4 border-b border-emerald-200 flex justify-between items-center">
              <h3 className="font-bold text-lg text-slate-800">Karta Pacjenta</h3>
              <button onClick={() => setSelectedPatient(null)} className="text-slate-400 hover:text-slate-600">
                <CloseIcon />
              </button>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <label className="text-xs text-slate-400 uppercase font-bold">Imię i Nazwisko</label>
                <p className="text-lg font-medium text-slate-900">{selectedPatient.firstName} {selectedPatient.lastName}</p>
              </div>
              <div>
                <label className="text-xs text-slate-400 uppercase font-bold">PESEL</label>
                <p className="font-mono text-slate-600 bg-emerald-100 inline-block px-2 py-0.5 rounded">{selectedPatient.pesel}</p>
              </div>
              <div className="pt-2 border-t border-slate-100">
                <label className="text-xs text-slate-400 uppercase font-bold">Adres</label>
                <p className="text-slate-700">
                  ul. {selectedPatient.street}<br/>
                  {selectedPatient.postalCode} {selectedPatient.city}
                </p>
              </div>
            </div>
            <div className="bg-emerald-50 px-6 py-3 border-t border-emerald-200 text-right">
              <button 
                onClick={() => setSelectedPatient(null)}
                className="text-sm font-medium text-slate-600 hover:text-slate-900 px-4 py-2"
              >
                Zamknij
              </button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL ZE SZCZEGÓŁAMI GABINETU */}
      {selectedRoom && (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center p-4 z-50 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-2xl max-w-lg w-full overflow-hidden animate-fade-in-up">
            <div className="bg-purple-50 px-6 py-4 border-b border-purple-200 flex justify-between items-center">
              <h3 className="font-bold text-lg text-slate-800">Szczegóły Gabinetu</h3>
              <button onClick={() => setSelectedRoom(null)} className="text-slate-400 hover:text-slate-600">
                <CloseIcon />
              </button>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <label className="text-xs text-slate-400 uppercase font-bold">Numer Pokoju</label>
                <p className="text-lg font-medium text-slate-900">Pokój {selectedRoom.roomNumber}</p>
              </div>
              
              {selectedRoom.schedules && selectedRoom.schedules.length > 0 && (
                <div className="pt-2 border-t border-slate-100">
                  <label className="text-xs text-slate-400 uppercase font-bold mb-3 block">Harmonogram Dyżurów</label>
                  <div className="space-y-2 max-h-60 overflow-y-auto">
                    {selectedRoom.schedules.map((schedule, idx) => (
                      <div key={idx} className="bg-purple-50 p-3 rounded-lg">
                        <p className="font-medium text-slate-800">
                          {schedule.doctor?.firstName} {schedule.doctor?.lastName}
                          {schedule.doctor?.specialization && (
                            <span className="ml-2 text-xs text-purple-600">({schedule.doctor.specialization})</span>
                          )}
                        </p>
                        <p className="text-sm text-slate-600">
                          {schedule.dutyTime?.shiftStart?.slice(0, 5)} - {schedule.dutyTime?.shiftEnd?.slice(0, 5)}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
              
              {(!selectedRoom.schedules || selectedRoom.schedules.length === 0) && (
                <div className="pt-2 border-t border-slate-100">
                  <p className="text-slate-400 text-sm">Brak zaplanowanych dyżurów w tym gabinecie.</p>
                </div>
              )}
            </div>
            <div className="bg-purple-50 px-6 py-3 border-t border-purple-200 text-right">
              <button 
                onClick={() => setSelectedRoom(null)}
                className="text-sm font-medium text-slate-600 hover:text-slate-900 px-4 py-2"
              >
                Zamknij
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

const inputStyles = document.createElement('style');
inputStyles.innerHTML = `
  .input-field {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.5rem;
    outline: none;
    transition: all 0.2s;
  }
  .input-field:focus {
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
`;
document.head.appendChild(inputStyles);

export default App;