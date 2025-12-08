import React, { useState, useEffect } from 'react';

// Ikony
const UserIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>;
const TrashIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>;
const EyeIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>;
const PlusIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>;
const CloseIcon = () => <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>;

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
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    pesel: '',
    specialization: 'INTERNAL_MEDICINE', // Domyślna wartość
    street: '',
    city: '',
    postalCode: ''
  });

  const API_URL = 'http://localhost:8080/doctors';

  useEffect(() => {
    fetchDoctors();
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
        alert("Nie udało się pobrać szczegółów (404 Not Found).");
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
        alert("Błąd usuwania. Sprawdź czy masz endpoint DELETE w kontrolerze.");
      }
    } catch (err) {
      console.error(err);
    }
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

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 p-6 font-sans">
      <div className="max-w-5xl mx-auto">
        
        {/* NAGŁÓWEK */}
        <header className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Baza Lekarzy</h1>
            <p className="text-slate-500 mt-1">System zarządzania personelem</p>
          </div>
          <button 
            onClick={() => setIsFormOpen(!isFormOpen)}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-lg font-medium transition shadow-sm"
          >
            {isFormOpen ? <CloseIcon /> : <PlusIcon />}
            {isFormOpen ? 'Zamknij formularz' : 'Dodaj lekarza'}
          </button>
        </header>

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

      </div>

      {/* MODAL ZE SZCZEGÓŁAMI */}
      {selectedDoctor && (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center p-4 z-50 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-2xl max-w-md w-full overflow-hidden animate-fade-in-up">
            <div className="bg-slate-50 px-6 py-4 border-b border-slate-200 flex justify-between items-center">
              <h3 className="font-bold text-lg text-slate-800">Karta Lekarza</h3>
              <button onClick={() => setSelectedDoctor(null)} className="text-slate-400 hover:text-slate-600">
                <CloseIcon />
              </button>
            </div>
            <div className="p-6 space-y-4">
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