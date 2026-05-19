DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctor;

CREATE TABLE doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255)
);

CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    diagnosis VARCHAR(255),
    doctor_id BIGINT,
    CONSTRAINT fk_patient_doctor
    FOREIGN KEY (doctor_id)
    REFERENCES doctor(id)
    ON DELETE SET NULL
);

INSERT INTO doctor (name, specialization) VALUES
('Bác sĩ Nguyễn Văn A', 'Nội khoa'),
('Bác sĩ Trần Thị B', 'Ngoại khoa');

INSERT INTO patients (full_name, diagnosis, doctor_id) VALUES
('Nguyễn Văn Khách', 'Viêm họng', 1),
('Lê Văn Nhân', 'Đau dạ dày', 1),
('Trần Văn Tâm', 'Chấn thương phần mềm', 2);
