# Smart Clinic System — Database Schema Design
This document outlines the hybrid database strategy for the Smart Clinic System, utilizing MySQL for structured relational data and MongoDB for flexible, document-oriented storage.

## 1. MySQL Database Design
MySQL is used to manage core relational entities where data consistency, structured relationships, and strong referential integrity are essential.

### *Tables Overview & SQL Definitions*
1. `patients`
   
Stores primary identity, authentication credentials, contact details, and administrative data required for patient management and billing.

``` sql 
CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,               -- Unique identifier for authentication
    password_hash VARCHAR(255) NOT NULL,             -- Hashed password for security
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_PATIENT', -- Security access level
    phone_number VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    address VARCHAR(100) NOT NULL,                    -- Required for invoicing and contact
    insurance_number VARCHAR(100) NOT NULL,           -- Required for medical coverage processing
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

2. `doctors`
   
Stores doctor profiles, medical license validation numbers, specializations, and access credentials.

``` sql
CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,               -- Unique email for login
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_DOCTOR',
    phone_number VARCHAR(20) NOT NULL,
    specialization VARCHAR(50) NOT NULL,             -- Medical specialization profile
    license_number VARCHAR(50) UNIQUE NOT NULL,       -- Unique legal medical identification
    room_number VARCHAR(10),                          -- Assigned office/cabinet number
    is_active BOOLEAN DEFAULT TRUE,                   -- Indicates current employment status
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

3. `appointments`
   
Manages scheduling links between patients and doctors, tracking consultation types and current appointment statuses.

``` sql
CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,                          -- Foreign key linking to patients table
    doctor_id INT NOT NULL,                           -- Foreign key linking to doctors table
    type VARCHAR(50) NOT NULL,                        -- Visit type (e.g., Initial, Follow-up)
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- Status tracking (SCHEDULED, COMPLETED, CANCELLED)
    appointment_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);
```

4. `admins`
   
Stores system administrators responsible for overall clinic management, user maintenance, and settings.

```sql
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 2. MongoDB Collection Design
MongoDB is selected for entities with variable or deeply nested structures that benefit from faster document reads without complex relational JOIN operations.

Chosen Collection: `prescriptions`

Medical prescriptions are ideal for a document model because a single prescription can contain multiple prescribed drugs, custom dosages, and historical context.

Sample JSON Document

``` json
{
  "_id": "6774893a9f1b2c3d4e5f6a7b",
  "status": "active",
  "createdAt": "2026-08-25T14:34:00Z",
  "validUntil": "2026-09-25T23:59:59Z",
  "patient": {
    "patientId": 1,
    "name": "Jane Black",
    "address": "At Some Street 2"
  },
  "doctor": {
    "doctorId": 3,
    "name": "Rick Hue",
    "specialization": "Dentist"
  },
  "medications": [
    {
      "medicineName": "OralB Gel 3mg",
      "dosage": "Apply twice daily after brushing",
      "durationDays": 7
    },
    {
      "medicineName": "Ibuprofen 400mg",
      "dosage": "1 tablet every 8 hours if pain occurs",
      "durationDays": 5
    }
  ],
  "notes": "Patient should avoid drinking coffee for 2 hours after applying the gel."
}
```

## Design Decision & Justification
- Embedded Data Model: Using an embedded array (medications) eliminates the need for separate drug-to-prescription join tables, keeping all treatment information in a single atomic record.

- Document Immutability: Storing basic patient and doctor snapshots inside the prescription preserves historical data integrity even if the primary patient address or doctor status changes in the relational database later.
