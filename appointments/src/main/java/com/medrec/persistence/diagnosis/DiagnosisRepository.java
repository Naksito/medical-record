package com.medrec.persistence.diagnosis;

import com.medrec.exception_handling.exceptions.BadRequestException;
import com.medrec.exception_handling.exceptions.DatabaseConnectionException;
import com.medrec.exception_handling.exceptions.DatabaseException;
import com.medrec.exception_handling.exceptions.NotFoundException;
import com.medrec.persistence.DBUtils;
import com.medrec.persistence.icd.Icd;
import com.medrec.persistence.leave.SickLeave;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class DiagnosisRepository {
    private static DiagnosisRepository instance;

    private final Logger logger = Logger.getLogger(DiagnosisRepository.class.getName());

    private DiagnosisRepository() {
    }

    public static DiagnosisRepository getInstance() {
        if (instance == null) {
            instance = new DiagnosisRepository();
        }
        return instance;
    }

    public Diagnosis save(String treatmentDescription, int icdId, Optional<SickLeave> leave) throws RuntimeException {
        this.logger.info("Saving diagnosis");

        if (treatmentDescription.isBlank() || icdId < 1) {
            this.logger.severe("Treatment description or icd id is invalid");
            throw new BadRequestException("invalid_treatment_description_or_icd_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Icd icd = session.get(Icd.class, icdId);
            if (icd == null) {
                throw new NotFoundException("icd_not_found");
            }

            Diagnosis diagnosis = new Diagnosis(treatmentDescription, icd, leave.orElse(null));

            session.persist(diagnosis);
            tx.commit();
            this.logger.info("Diagnosis saved successfully");
            return diagnosis;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public Diagnosis getById(int id) throws RuntimeException {
        this.logger.info("Getting diagnosis with id " + id);
        if (id < 1) {
            this.logger.severe("Diagnosis id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Diagnosis diagnosis = session.get(Diagnosis.class, id);
            if (diagnosis == null) {
                this.logger.severe("Could not find diagnosis with id " + id);
                throw new NotFoundException("diagnosis_not_found");
            }
            tx.commit();

            this.logger.info("Diagnosis found successfully");
            return diagnosis;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            this.logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Not found exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public List<Diagnosis> findAll() throws RuntimeException {
        this.logger.info("Getting all diagnoses");

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "SELECT d FROM Diagnosis d";
            List<Diagnosis> diagnoses = session.createQuery(hql, Diagnosis.class).getResultList();
            tx.commit();
            return diagnoses;
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

    public Diagnosis update(int diagnosisId, Optional<String> treatmentDescription, Optional<Integer> icdId, Optional<SickLeave> leave) throws RuntimeException {
        this.logger.info("Updating diagnosis with id " + diagnosisId);
        if ((diagnosisId < 1) || (icdId.isPresent() && icdId.get() < 1)) {
            this.logger.severe("Diagnosis id or ICD id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Diagnosis diagnosis = session.get(Diagnosis.class, diagnosisId);
            if (diagnosis == null) {
                this.logger.severe("Could not find diagnosis with id " + diagnosisId);
                throw new NotFoundException("diagnosis_not_found");
            }

            treatmentDescription.ifPresent(diagnosis::setTreatmentDescription);

            leave.ifPresent(diagnosis::setSickLeave);

            if (icdId.isPresent()) {
                Icd icd = session.get(Icd.class, icdId.get());
                if (icd == null) {
                    throw new NotFoundException("icd_not_found");
                }

                diagnosis.setIcd(icd);
            }

            session.merge(diagnosis);
            tx.commit();

            this.logger.info("Diagnosis updated successfully");
            return diagnosis;
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
        this.logger.info("Deleting diagnosis with id " + id);
        if (id < 1) {
            this.logger.severe("Diagnosis id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Diagnosis diagnosis = session.get(Diagnosis.class, id);
            if (diagnosis == null) {
                this.logger.severe("Could not find diagnosis with id " + id);
                throw new NotFoundException("diagnosis_not_found");
            }
            session.delete(diagnosis);
            tx.commit();

            this.logger.info("Diagnosis deleted successfully");
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
