# Medical Clinic Management System

A full-stack application designed to manage medical clinic operations, including staff scheduling, patient registration, and consulting room equipment tracking.

## Tech Stack

### Backend
* **Framework:** Spring Boot 3
* **Language:** Java 21+
* **Database Access:** Spring Data JPA (via Repositories for Doctors, Patients, Rooms, and Schedules)
* **Error Handling:** Global REST Controller Advice for validation and exception mapping

### Frontend
* **Library:** React.js
* **Styling:** Tailwind CSS
* **Icons:** Custom SVG components for a clean medical UI

---

## Key Features

###  Doctor Management
* Maintain a database of medical professionals with detailed profiles.
* Support for multiple specializations including Cardiology, Dermatology, Allergology, General Surgery, and more.
* Store doctor information: Name, PESEL, Specialization, and full Address.

### Consulting Room Tracking
* Manage physical rooms (e.g., Room 101, 201).
* Track specialized medical facilities per room:
    * Examination Beds
    * ECG Machines
    * Scales and Thermometers
    * Diagnostic Sets

### Advanced Scheduling
* Check real-time availability for both doctors and rooms within specific time frames.
* Prevent scheduling conflicts by validating shift start and end times.
* View specific schedules directly within the Doctor or Room detail modals.

### Patient Registry
* Register patients with personal data, PESEL identification, and contact addresses.
* Quick-view patient cards for administrative efficiency.

---

## Database Utilities

The project includes specialized components for managing the data lifecycle:
* **DatabaseInitializer:** Automatically clears existing records and seeds the database with dummy doctors (e.g., John Doe, Jane Smith), rooms, and patients for testing purposes.
* **DatabaseClean:** A standalone utility to completely wipe the database.

---

## Getting Started

### Prerequisites
* JDK 21 or higher
* Node.js and npm
* Gradle (or use the included Gradle Wrapper)

### Installation

1. **Backend:**
   * Navigate to the `backend-spring-boot` directory.
   * Run the application: 
     ```bash
     ./gradlew bootRun
     ```
   * The API will be available at `http://localhost:8080`.

2. **Frontend:**
   * Navigate to the frontend folder.
   * Install dependencies:
     ```bash
     npm install
     ```
   * Start the development server:
     ```bash
     npm start
     ```
   * Access the UI at `http://localhost:3000`.

---

## API Overview

The React frontend communicates with the following REST endpoints:
* `/doctors` - CRUD operations for medical staff.
* `/patients` - Management of patient records.
* `/consulting-room` - Management of room equipment and availability.
* `/schedules` - Assignment of shifts and availability checks.
* `/visits` - Visit availability, booking, and cancellation.
