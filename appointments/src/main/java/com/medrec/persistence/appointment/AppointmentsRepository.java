package com.medrec.persistence.appointment;

import com.medrec.dtos.appointment.DoctorAppointmentsCountDTO;
import com.medrec.dtos.appointment.DoctorSickLeaveCountDTO;
import com.medrec.dtos.appointment.MonthWithMostSickLeavesDTO;
import com.medrec.exception_handling.exceptions.*;
import com.medrec.gateway.UsersGateway;
import com.medrec.persistence.DBUtils;
import com.medrec.persistence.diagnosis.Diagnosis;
import com.medrec.persistence.icd.Icd;
import com.medrec.persistence.leave.SickLeave;
import com.medrec.utils.CascadeEntityType;
import com.medrec.utils.Utils;
import io.grpc.StatusRuntimeException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class AppointmentsRepository {
    private static AppointmentsRepository instance;

    private final UsersGateway usersGateway = UsersGateway.getInstance();

    private final Logger logger = Logger.getLogger(AppointmentsRepository.class.getName());

    private AppointmentsRepository() {}

    public static AppointmentsRepository getInstance() {
        if (instance == null) {
            instance = new AppointmentsRepository();
        }
        return instance;
    }

    private boolean isDoctorFree(int doctorId, LocalDateTime dateTime, Session session, Transaction tx) {
        String hql = "SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.dateTime = :dateTime";
        Appointment appointment = (Appointment) session.createQuery(hql)
            .setParameter("doctorId", doctorId)
            .setParameter("dateTime", dateTime)
            .uniqueResult();

        return appointment == null;
    }

    public Appointment save(String date, String time, int doctorId, int patientId) throws RuntimeException {
        logger.info("Saving new appointment");
        String dateTime = date + "T" + time;

        if (doctorId < 1 || patientId < 1) {
            logger.severe("Doctor or patient id is invalid");
            throw new BadRequestException("invalid_id");
        }

        try {
            this.usersGateway.patientExists(patientId);
            this.usersGateway.doctorExists(doctorId);
        } catch (StatusRuntimeException e) {
            String message = e.getMessage();
            logger.severe("Users service returned exception: " + message);
            if (message.contains("Doctor")) {
                throw new NotFoundException("doctor_not_found");
            } else if (message.contains("Patient")) {
                throw new NotFoundException("patient_not_found");
            } else {
                throw e;
            }
        }

        Transaction tx = null;
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            if(!isDoctorFree(doctorId, localDateTime, session, tx)) {
                this.logger.warning("Doctor is not free at the specified time");
                throw new BadRequestException("doctor_not_free");
            }

            Appointment appointment = new Appointment(
                localDateTime,
                patientId,
                doctorId,
                "upcoming"
            );

            session.persist(appointment);
            tx.commit();

            logger.info("Appointment saved successfully");
            return appointment;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (DateTimeParseException e) {
            DBUtils.rollback(tx);
            logger.severe("Could not parse date and time: " + e.getMessage());
            throw new BadRequestException("date_time_not_parsed");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public Appointment getById(int id) throws RuntimeException {
        logger.info("Getting appointment with id " + id);
        if (id < 1) {
            logger.severe("Appointment id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Appointment appointment = session.get(Appointment.class, id);
            if (appointment == null) {
                this.logger.severe("Could not find appointment with id " + id);
                throw new NotFoundException("appointment_not_found");
            }
            tx.commit();

            logger.info("Appointment found successfully");
            return appointment;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Appointment> findAllByPatientEmail(String email) throws RuntimeException {
        logger.info("Getting appointments for patient with email " + email);

        Transaction tx = null;
        try {
            int patientId = this.usersGateway.getPatientIdByEmail(email);

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "SELECT a FROM Appointment a WHERE a.patientId=:patientId";
            List<Appointment> appointments = session.createQuery(hql, Appointment.class)
                .setParameter("patientId", patientId)
                .getResultList();
            tx.commit();
            return appointments;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (StatusRuntimeException e) {
            String message = e.getMessage();
            logger.severe("Users service returned exception: " + message);
            if (message.contains("Patient")) {
                throw new NotFoundException("patient_not_found");
            } else {
                throw e;
            }
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Appointment> findAllByPatientId(int id) throws RuntimeException {
        logger.info("Getting appointments for patient with id " + id);

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "SELECT a FROM Appointment a WHERE a.patientId=:patientId ORDER BY dateTime DESC";
            List<Appointment> appointments = session.createQuery(hql, Appointment.class)
                .setParameter("patientId", id)
                .getResultList();
            tx.commit();
            return appointments;
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

    public List<Appointment> findAllByDoctorEmail(String email) throws RuntimeException {
        logger.info("Getting appointments for doctor with email " + email);

        Transaction tx = null;
        try {
            int doctorId = this.usersGateway.getDoctorIdByEmail(email);

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "SELECT a FROM Appointment a WHERE a.doctorId=:doctorId ORDER BY dateTime DESC";
            List<Appointment> appointments = session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId)
                .getResultList();
            tx.commit();
            return appointments;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (StatusRuntimeException e) {
            String message = e.getMessage();
            logger.severe("Users service returned exception: " + message);
            if (message.contains("Doctor")) {
                throw new NotFoundException("doctor_not_found");
            } else {
                throw e;
            }
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Appointment> findAllByDoctorId(int id) throws RuntimeException {
        logger.info("Getting appointments for doctor with id " + id);

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "SELECT a FROM Appointment a WHERE a.doctorId=:doctorId";
            List<Appointment> appointments = session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", id)
                .getResultList();
            tx.commit();
            return appointments;
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

    public List<Appointment> findAll() throws RuntimeException {
        logger.info("Getting all appointments");

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            List<Appointment> appointments = session.createQuery("FROM Appointment", Appointment.class).getResultList();
            tx.commit();
            return appointments;
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

    public Appointment update(int id, Optional<String> status, Optional<Integer> diagnosisId) throws RuntimeException {
        logger.info("Updating appointment with id " + id);

        if (id < 1) {
            logger.severe("Appointment id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Appointment appointment = session.get(Appointment.class, id);
            if (appointment == null) {
                this.logger.severe("Could not find appointment with id " + id);
                throw new NotFoundException("appointment_not_found");
            }

            if (status.isPresent()) {
                if (!status.get().equals("upcoming") && !status.get().equals("started") && !status.get().equals("finished")) {
                    logger.severe("Appointment status is invalid");
                    throw new BadRequestException("invalid_status");
                }
                appointment.setStatus(status.get());
            }

            if (diagnosisId.isPresent()) {
                int doctorId = diagnosisId.get();


                if (doctorId == -1) {
                    appointment.setDiagnosis(null);
                } else {
                    Diagnosis diagnosis = session.get(Diagnosis.class, doctorId);
                    if (diagnosis == null) {
                        this.logger.severe("Could not find diagnosis with id " + doctorId);
                        throw new NotFoundException("diagnosis_not_found");
                    }
                    appointment.setDiagnosis(diagnosis);
                }
            }

            tx.commit();

            logger.info("Appointment status updated successfully");
            return appointment;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public void delete(int id) throws RuntimeException {
        logger.info("Deleting appointment with id " + id);

        if (id < 1) {
            logger.severe("Appointment id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Appointment appointment = session.get(Appointment.class, id);
            if (appointment == null) {
                this.logger.severe("Could not find appointment with id " + id);
                throw new NotFoundException("appointment_not_found");
            }
            session.delete(appointment);
            tx.commit();

            this.logger.info("Appointment deleted successfully");
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public void genericCascadeDelete(int entityId, CascadeEntityType type) throws RuntimeException {

        logger.info("Cascading appointments for " + type + " with id " + entityId);

        if (entityId < 1) {
            logger.severe("Id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            int num = switch (type) {
                case PATIENT -> {
                    String hql = "DELETE FROM Appointment a WHERE a.patientId=:patientId";
                    yield session.createQuery(hql, Appointment.class).setParameter("patientId", entityId).executeUpdate();
                }
                case DOCTOR -> {
                    String hql = "DELETE FROM Appointment a WHERE a.doctorId=:doctorId";
                    yield session.createQuery(hql, Appointment.class).setParameter("doctorId", entityId).executeUpdate();
                }
            };

            tx.commit();
            this.logger.info(String.format("Successfully deleted %d appointments for %s with id %d", num, type, entityId));
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Icd> startAppointmentFetchIcds(int appointmentId, int doctorId) throws RuntimeException {
        this.logger.info(String.format("Starting appointment with id %d", appointmentId));
        if (appointmentId < 1 || doctorId < 1) {
            logger.severe("Appointment id or Doctor id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Appointment appointment = session.get(Appointment.class, appointmentId);

            if (appointment == null) {
                this.logger.severe("Could not find appointment with id " + appointmentId);
                throw new NotFoundException("appointment_not_found");
            }

            if (appointment.getDoctorId() != doctorId) {
                this.logger.severe("Appointment doctor id mismatch");
                throw new AbortedException("doctor_has_no_access_to_appointment");
            }

            appointment.setStatus("started");

            String hql = "SELECT i FROM Icd i";
            List<Icd> icds = session.createQuery(hql, Icd.class).getResultList();

            tx.commit();

            logger.info(String.format("Successfully started appointment with id %d", appointmentId));
            logger.info("Found " + icds.size() + " icds for page");
            return icds;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (AbortedException e) {
            logger.severe("Aborted exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public void finishAppointmentAddDiagnosis(
        int appointmentId,
        String treatmentDescription,
        int icdId,
        Optional<String> sickLeaveDate,
        Optional<Integer> sickLeaveDays
    ) throws RuntimeException {
        logger.info(String.format("Finishing appointment with id %d", appointmentId));

        if (appointmentId < 1 || icdId < 1) {
            logger.severe("Appointment id or icd id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Appointment appointment = session.get(Appointment.class, appointmentId);
            if (appointment == null) {
                this.logger.severe("Could not find appointment with id " + appointmentId);
                throw new NotFoundException("appointment_not_found");
            }

            Icd icd = session.get(Icd.class, icdId);
            if (icd == null) {
                this.logger.severe("Could not find icd with id " + icdId);
                throw new NotFoundException("icd_not_found");
            }

            Diagnosis diagnosis;
            if (sickLeaveDate.isPresent() && sickLeaveDays.isPresent()) {
                LocalDate date = Utils.parseDate(sickLeaveDate.get());
                SickLeave sickLeave = new SickLeave(
                    date,
                    sickLeaveDays.get()
                );

                diagnosis = new Diagnosis(treatmentDescription, icd, sickLeave);
            } else {
                diagnosis = new Diagnosis(treatmentDescription, icd);
            }

            session.persist(diagnosis);

            appointment.setDiagnosis(diagnosis);
            appointment.setStatus("finished");

            tx.commit();

            this.logger.info(String.format("Successfully finished appointment with id %d", appointmentId));
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Integer> getAllPatientIdsForIcd(int icdId) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Icd icd = session.get(Icd.class, icdId);
            if (icd == null) {
                this.logger.severe("Could not find icd with id " + icdId);
                throw new NotFoundException("icd_not_found");
            }

            Query query = session.createNativeQuery(
                "SELECT DISTINCT a.patient_id " +
                "FROM appointment a " +
                "LEFT JOIN diagnosis d ON d.id=a.diagnosis_id " +
                "WHERE d.icd_id=:icdId")
                .setParameter("icdId", icdId);
            List<Integer> patientIds = query.getResultList();

            tx.commit();
            this.logger.info(String.format("Successfully retrieved %d patient IDs for ICD %d", patientIds.size(), icdId));
            return patientIds;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<DoctorAppointmentsCountDTO> getDoctorAppointmentsCount() throws RuntimeException {
        this.logger.info("Retrieving count for appointments for all doctors");

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Query query = session.createNativeQuery(
                "SELECT doctor_id, COUNT(*) AS appointments_count \n" +
                "FROM appointment\n" +
                "GROUP BY doctor_id"
            );

            List<Object[]> list = query.getResultList();
            List<DoctorAppointmentsCountDTO> doctorAppointmentsCountDTOS = new ArrayList<>();
            for (Object[] row : list) {
                DoctorAppointmentsCountDTO appointmentsCountDTO = new DoctorAppointmentsCountDTO();
                appointmentsCountDTO.setDoctorId((Integer) row[0]);
                appointmentsCountDTO.setAppointmentsCount(Math.toIntExact((Long) row[1]));
                doctorAppointmentsCountDTOS.add(appointmentsCountDTO);
            }

            tx.commit();

            this.logger.info("Successfully retrieved count for all appointments for all doctors");
            return doctorAppointmentsCountDTOS;
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

    public List<Appointment> getAppointmentsForTimePeriod(
        String startDate,
        String endDate,
        Optional<Integer> doctorId
    ) throws RuntimeException {
        Transaction tx = null;
        try {
            LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            List<Appointment> appointments;
            if (doctorId.isPresent()) {
                String hql = "SELECT a " +
                    "FROM Appointment a " +
                    "WHERE a.doctorId=:doctorId " +
                    "AND dateTime>=:startDateTime " +
                    "AND dateTime<=:endDateTime";
                appointments = session.createQuery(hql, Appointment.class)
                    .setParameter("startDateTime", startDateTime)
                    .setParameter("endDateTime", endDateTime)
                    .setParameter("doctorId", doctorId.get())
                    .getResultList();;
            } else {
                String hql = "SELECT a " +
                    "FROM Appointment a " +
                    "WHERE dateTime>=:startDateTime " +
                    "AND dateTime<=:endDateTime";
                appointments = session.createQuery(hql, Appointment.class)
                    .setParameter("startDateTime", startDateTime)
                    .setParameter("endDateTime", endDateTime)
                    .getResultList();
            }

            tx.commit();

            this.logger.info("Successfully retrieved appointments for time period: " + startDate + " - " + endDate);
            return appointments;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (DateTimeParseException e) {
            DBUtils.rollback(tx);
            logger.severe("Could not parse date and time: " + e.getMessage());
            throw new BadRequestException("date_time_not_parsed");
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public MonthWithMostSickLeavesDTO getMonthWithMostSickLeaves(String yearStr) throws RuntimeException {
        Transaction tx = null;

        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Query query = session.createNativeQuery(
                "SELECT " +
                "TO_CHAR(DATE_TRUNC('month', a.datetime), 'YYYY-MM') AS date, " +
                "COUNT(*) AS appointments_count " +
                "FROM appointment a " +
                "JOIN diagnosis d ON a.diagnosis_id=d.id " +
                "WHERE d.sick_leave_id IS NOT NULL " +
                "AND EXTRACT(YEAR FROM a.datetime)=:year " +
                "GROUP BY date, a.diagnosis_id " +
                "ORDER BY appointments_count DESC " +
                "LIMIT 1"
            ).setParameter("year", Integer.parseInt(yearStr));

            List<Object[]> list = query.getResultList();
            if (list.isEmpty()) {
                return new MonthWithMostSickLeavesDTO(yearStr, 0);
            }

            MonthWithMostSickLeavesDTO dto = new MonthWithMostSickLeavesDTO();
            for (Object[] row : list) {
                dto.setDate((String) row[0]);
                dto.setCount(Math.toIntExact((Long) row[1]));
            }

            tx.commit();

            this.logger.info("Successfully retrieved month with most sick leaves for year: " + yearStr);
            return dto;
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

    public List<DoctorSickLeaveCountDTO> getDoctorsBySickLeaveCount(int limit) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Query query = session.createNativeQuery(
                "SELECT a.doctor_id, COUNT(*) count " +
                "FROM appointment a " +
                "JOIN diagnosis d ON d.id = a.diagnosis_id " +
                "WHERE d.sick_leave_id IS NOT NULL " +
                "GROUP BY a.doctor_id " +
                "ORDER BY count DESC " +
                "LIMIT :limit"
            ).setParameter("limit", limit);

            List<Object[]> list = query.getResultList();

            if (list.isEmpty()) {
                return new ArrayList<>();
            }

            List<DoctorSickLeaveCountDTO> dtos = new ArrayList<>();
            for (Object[] row : list) {
                dtos.add(new DoctorSickLeaveCountDTO(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue()
                ));
            }

            tx.commit();

            this.logger.info("Successfully retrieved most sick leaves by doctors with limit: " + limit);
            return dtos;
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
