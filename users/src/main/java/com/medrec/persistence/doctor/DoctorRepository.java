package com.medrec.persistence.doctor;

import com.medrec.dtos.CreateDoctorDTO;
import com.medrec.dtos.PatientCountDTO;
import com.medrec.exception_handling.exceptions.*;
import com.medrec.grpc.users.Users;
import com.medrec.persistence.DBUtils;
import com.medrec.persistence.ICrudRepository;
import com.medrec.persistence.specialty.Specialty;
import com.medrec.persistence.specialty.SpecialtyRepository;
import jakarta.persistence.EntityExistsException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class DoctorRepository implements ICrudRepository<Doctor, CreateDoctorDTO, Users.UpdateDoctorRequest> {
    private static DoctorRepository instance;

    private final SpecialtyRepository specialtyRepository = SpecialtyRepository.getInstance();

    private final Logger logger = Logger.getLogger(DoctorRepository.class.getName());

    private DoctorRepository() {}

    public static DoctorRepository getInstance() {
        if (instance == null) {
            instance = new DoctorRepository();
        }

        return instance;
    }

    @Override
    public Doctor save(CreateDoctorDTO dto) throws RuntimeException {
        Transaction tx = null;
        try {
            Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId());

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Doctor doctor = dto.createDoctorWithSpecialty(specialty);
            session.persist(doctor);
            tx.commit();

            this.logger.info("Doctor saved successfully");
            return doctor;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Specialty with id + " + dto.getSpecialtyId() + " not found: " + e.getMessage());
            throw e;
        } catch (ConstraintViolationException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Constraint violation: " + e.getMessage());
            throw new ConstrainException("Constraint exception");
        } catch (EntityExistsException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Entity already exists: " + e.getMessage());
            throw new AlreadyExistsException("Entity already exists");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    @Override
    public Doctor findById(int id) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Doctor doctor = session.get(Doctor.class, id);
            tx.commit();

            if (doctor == null) {
                throw new NotFoundException("doctor_not_found");
            }

            this.logger.info(String.format("Doctor with id %s found", id));
            return doctor;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Doctor with id " + id + " not found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public Doctor findByEmail(String email) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "from Doctor where email = :email";
            Query query = session.createQuery("from Doctor where email = :email")
                .setParameter("email", email);
            Doctor doctor = (Doctor) query.uniqueResult();
            tx.commit();

            if (doctor == null) {
                throw new NotFoundException("doctor_not_found");
            }

            this.logger.info(String.format("Doctor with email %s found", email));
            return doctor;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Doctor with email + " + email + " not found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    @Override
    public List<Doctor> findAll() throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            List<Doctor> doctors = session.createQuery("from Doctor", Doctor.class).getResultList();
            tx.commit();
            return doctors;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    @Override
    public Doctor update(Users.UpdateDoctorRequest request) throws RuntimeException {
        int id = request.getDoctorId();
        Optional<Doctor> doctorOptional = Optional.ofNullable(findById(id));

        Transaction tx = null;
        try {
            if (doctorOptional.isEmpty()) {
                throw new NotFoundException("doctor_not_found");
            }

            Doctor doctor = doctorOptional.get();

            if (request.hasFirstName()) {
                doctor.setFirstName(request.getFirstName());
            }

            if (request.hasLastName()) {
                doctor.setLastName(request.getLastName());
            }

            if (request.hasPassword()) {
                doctor.setPassword(request.getPassword());
            }

            if (request.hasIsGp()) {
                doctor.setGp(request.getIsGp());
            }

            if (request.hasSpecialtyId()) {
                Optional<Specialty> optionalSpecialty = Optional.ofNullable(specialtyRepository.findById(request.getSpecialtyId()));
                if (optionalSpecialty.isEmpty()) {
                    throw new NotFoundException("specialty_not_found");
                }
                doctor.setSpecialty(optionalSpecialty.get());
            }

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Doctor updatedDoctor = session.merge(doctor);
            tx.commit();

            this.logger.info("Doctor updated successfully");
            return updatedDoctor;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Not found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    @Override
    public void delete(int id) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Doctor doctor = session.get(Doctor.class, id);

            if (doctor == null) {
                throw new NotFoundException("doctor_not_found");
            }

            session.remove(doctor);
            tx.commit();

            this.logger.info(String.format("Doctor %s deleted successfully", doctor.toString()));
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe(String.format("Specialty with id + %s not found", id));
            throw e;
        } catch (ConstraintViolationException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Constraint exception: " + e.getMessage());
            throw new ConstrainException("Constraint exception");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Doctor> findAllGpDoctors() throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            List<Doctor> doctors = session.createQuery("from Doctor where isGp=true", Doctor.class).getResultList();
            tx.commit();
            return doctors;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<PatientCountDTO> countOfPatientsForDoctors() throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Query query = session.createNativeQuery("SELECT d.id AS doctor_id, d.first_name, d.last_name, COUNT(p.id) AS patient_count FROM doctor d LEFT JOIN patient p ON d.id=p.doctor_id WHERE d.is_gp = TRUE GROUP BY d.id ORDER BY patient_count");
            List<Object[]> list = query.getResultList();
            List<PatientCountDTO> patientCountDTOList = new ArrayList<>();
            for (Object[] row : list) {
                PatientCountDTO patientCountDTO = new PatientCountDTO();
                patientCountDTO.setDoctorId((Integer) row[0]);
                patientCountDTO.setDoctorFirstName((String) row[1]);
                patientCountDTO.setDoctorLastName((String) row[2]);
                patientCountDTO.setPatientCount(Math.toIntExact((Long) row[3]));
                patientCountDTOList.add(patientCountDTO);
            }
            tx.commit();

            patientCountDTOList.forEach(System.out::println);

            this.logger.info(String.format("Query list size: %d", patientCountDTOList.size()));
            return patientCountDTOList;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }
}
