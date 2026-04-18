package com.medrec.persistence.leave;

import com.medrec.exception_handling.exceptions.BadRequestException;
import com.medrec.exception_handling.exceptions.DatabaseConnectionException;
import com.medrec.exception_handling.exceptions.DatabaseException;
import com.medrec.exception_handling.exceptions.NotFoundException;
import com.medrec.persistence.DBUtils;
import com.medrec.persistence.appointment.Appointment;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SickLeaveRepository {
    public static SickLeaveRepository instance;

    private final Logger logger = Logger.getLogger(SickLeaveRepository.class.getName());

    private SickLeaveRepository() {}

    public static SickLeaveRepository getInstance() {
        if (instance == null) {
            instance = new SickLeaveRepository();
        }
        return instance;
    }

    public SickLeave save(String startDate, int daysOfLeave) throws RuntimeException {
        this.logger.info("Saving new Sick Leave");

        if (startDate.isBlank() || daysOfLeave < 1) {
            this.logger.severe("Start date or number of days leave is invalid");
            throw new BadRequestException("invalid_start_date_or_number_of_days_leave");
        }

        Transaction tx = null;
        try {
            LocalDate startDateLocal = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            SickLeave sickLeave = new SickLeave(startDateLocal, daysOfLeave);

            session.persist(sickLeave);
            tx.commit();

            this.logger.info("New Sick Leave saved successfully");
            return sickLeave;
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

    public SickLeave getById(int id) throws RuntimeException {
        this.logger.info("Getting Sick Leave with id " + id);

        if (id < 1) {
            this.logger.severe("Sick Leave id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            SickLeave sickLeave = session.get(SickLeave.class, id);
            if (sickLeave == null) {
                this.logger.severe("Could not find sick leave with id " + id);
                throw new NotFoundException("sick_leave_not_found");
            }
            tx.commit();

            logger.info("Sick leave found successfully");
            return sickLeave;
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

    public List<SickLeave> findAll() throws RuntimeException {
        this.logger.info("Getting all Sick Leave entities");

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            List<SickLeave> sickLeaveEntities = session.createQuery("FROM SickLeave", SickLeave.class).getResultList();
            tx.commit();
            return sickLeaveEntities;
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

    public SickLeave update(int id, Optional<String> startDate, Optional<Integer> daysOfLeave) throws RuntimeException {
        this.logger.info("Updating Sick Leave with id " + id);
        if (id < 1) {
            this.logger.warning("Id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            SickLeave sickLeave = session.get(SickLeave.class, id);
            if (sickLeave == null) {
                this.logger.severe("Could not find Sick Leave with id " + id);
                throw new NotFoundException("sick_leave_not_found");
            }

            if (startDate.isPresent() && !startDate.get().isBlank()) {
                LocalDate newDate = LocalDate.parse(startDate.get(), DateTimeFormatter.ISO_LOCAL_DATE);
                sickLeave.setStartDate(newDate);
            }

            if (daysOfLeave.isPresent()) {
                int daysOfLeaveInt = daysOfLeave.get();
                this.logger.info("Number of days leave is " + daysOfLeaveInt);
                if (daysOfLeaveInt < 1) {
                    this.logger.warning("Number of days leave is invalid");
                    throw new BadRequestException("invalid_number_of_days_leave");
                }
                sickLeave.setDaysOfLeave(daysOfLeaveInt);
            }

            session.merge(sickLeave);
            tx.commit();

            this.logger.info("Sick Leave updated successfully");
            return sickLeave;
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
        this.logger.info("Deleting Sick Leave entity with id " + id);

        if (id < 1) {
            this.logger.warning("Id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            SickLeave sickLeave = session.get(SickLeave.class, id);
            if (sickLeave == null) {
                this.logger.severe("Could not find Sick Leave with id " + id);
                throw new NotFoundException("sick_leave_not_found");
            }
            session.delete(sickLeave);
            tx.commit();

            this.logger.info("Sick Leave entity deleted successfully");
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
}
