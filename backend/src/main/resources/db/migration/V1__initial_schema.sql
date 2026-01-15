-- ===============================
-- USERS
-- ===============================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'DOCTOR', 'PATIENT'))
);

-- ===============================
-- PATIENTS
-- ===============================
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    gender VARCHAR(20),
    phone VARCHAR(50),
    address TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_patients_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'UNDISCLOSED')),
    CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ===============================
-- DOCTORS
-- ===============================
CREATE TABLE doctors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    bio TEXT,
    phone VARCHAR(50),
    office_address TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ===============================
-- SPECIALTIES
-- ===============================
CREATE TABLE specialties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- ===============================
-- DOCTOR_SPECIALTIES
-- ===============================
CREATE TABLE doctor_specialties (
    doctor_id BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    proficiency_level VARCHAR(50) NOT NULL DEFAULT 'JUNIOR',
    PRIMARY KEY (doctor_id, specialty_id),
    CONSTRAINT chk_ds_proficiency CHECK (proficiency_level IN ('JUNIOR', 'MID', 'SENIOR', 'EXPERT')),
    CONSTRAINT fk_ds_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT fk_ds_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);

-- ===============================
-- APPOINTMENTS
-- ===============================
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    location VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_appointment_status CHECK (status IN ('AVAILABLE', 'BOOKED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    CONSTRAINT chk_appointment_time CHECK (end_time > start_time)
);

-- ===============================
-- BOOKINGS
-- ===============================
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    booked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'NO_SHOW')),
    CONSTRAINT chk_booking_payment CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    CONSTRAINT fk_bookings_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- ===============================
-- DIAGNOSES
-- ===============================
CREATE TABLE diagnoses (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    notes TEXT,
    diagnosed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diagnoses_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE RESTRICT
);

-- ===============================
-- PRESCRIPTIONS
-- ===============================
CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    diagnosis_id BIGINT NOT NULL,
    prescribed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    CONSTRAINT fk_prescriptions_diagnosis FOREIGN KEY (diagnosis_id) REFERENCES diagnoses(id) ON DELETE RESTRICT
);

-- ===============================
-- MEDICATION
-- ===============================
CREATE TABLE medication (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    manufacturing_date DATE,
    expiration_date DATE
);

-- ===============================
-- PRESCRIPTION_MEDICATIONS
-- ===============================
CREATE TABLE prescription_medications (
    prescription_id BIGINT NOT NULL,
    medication_id BIGINT NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100) NOT NULL DEFAULT 'AS_NEEDED',
    duration_days INT,
    instructions TEXT,
    PRIMARY KEY (prescription_id, medication_id),
    CONSTRAINT chk_pm_frequency
        CHECK (frequency IN (
            'ONCE_DAILY',
            'TWICE_DAILY',
            'THREE_TIMES_DAILY',
            'WEEKLY',
            'AS_NEEDED'
        )),
    CONSTRAINT chk_pm_duration CHECK (duration_days IS NULL OR duration_days > 0),
    CONSTRAINT fk_pm_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    CONSTRAINT fk_pm_medication FOREIGN KEY (medication_id) REFERENCES medication(id)
);

-- ===============================
-- DISEASES
-- ===============================
CREATE TABLE diseases (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- ===============================
-- DIAGNOSIS_DISEASES
-- ===============================
CREATE TABLE diagnosis_diseases (
    diagnosis_id BIGINT NOT NULL,
    disease_id BIGINT NOT NULL,
    notes TEXT,
    PRIMARY KEY (diagnosis_id, disease_id),
    CONSTRAINT fk_dd_diagnosis FOREIGN KEY (diagnosis_id) REFERENCES diagnoses(id),
    CONSTRAINT fk_dd_disease FOREIGN KEY (disease_id) REFERENCES diseases(id)
);