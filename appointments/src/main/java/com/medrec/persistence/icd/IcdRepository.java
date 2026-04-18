package com.medrec.persistence.icd;

import com.medrec.dtos.icd.IcdOccurrenceDTO;
import com.medrec.exception_handling.exceptions.BadRequestException;
import com.medrec.exception_handling.exceptions.DatabaseConnectionException;
import com.medrec.exception_handling.exceptions.DatabaseException;
import com.medrec.exception_handling.exceptions.NotFoundException;
import com.medrec.grpc.appointments.Appointments;
import com.medrec.persistence.DBUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.logging.Logger;

public class IcdRepository {
    public static IcdRepository instance;

    private final Logger logger = Logger.getLogger(IcdRepository.class.getName());

    private IcdRepository() {}

    public static IcdRepository getInstance() {
        if (instance == null) {
            instance = new IcdRepository();
        }
        return instance;
    }

    public Icd save(String code, String description) throws RuntimeException {
        this.logger.info("Saving new ICD");

        if (code.isBlank() || description.isBlank()) {
            this.logger.severe("Code or description is invalid");
            throw new BadRequestException("invalid_code_or_description");
        }

        Icd icd = new Icd(code, description);

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            session.persist(icd);
            tx.commit();
            this.logger.info("New ICD saved successfully");
            return icd;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new RuntimeException("Exception found in database connection initialization!");
        } catch (BadRequestException e) {
            logger.severe("Bad request exception found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    public Icd getById(int id) throws RuntimeException {
        this.logger.info("Getting ICD with id " + id);
        if (id < 1) {
            this.logger.severe("ICD id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Icd icd = session.get(Icd.class, id);
            if (icd == null) {
                this.logger.severe("Could not find ICD with id " + id);
                throw new NotFoundException("icd_not_found");
            }
            tx.commit();

            this.logger.info("ICD found successfully");
            return icd;
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

    public List<Icd> findAll() throws RuntimeException {
        this.logger.info("Getting all ICDs");

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            String hql = "SELECT i FROM Icd i";
            List<Icd> icds = session.createQuery(hql, Icd.class).getResultList();
            tx.commit();
            return icds;
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

    public Icd update(int icdId, Optional<String> code, Optional<String> description) throws RuntimeException {
        this.logger.info("Updating ICD with id " + icdId);
        if (icdId < 1) {
            this.logger.severe("ICD id is invalid");
            throw new BadRequestException("invalid_if");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Icd icd = session.get(Icd.class, icdId);
            if (icd == null) {
                this.logger.severe("Could not find ICD with id " + icdId);
                throw new NotFoundException("icd_not_found");
            }

            if (code.isPresent() && !code.get().isBlank()) {
                icd.setCode(code.get());
            }

            if (description.isPresent() && !description.get().isBlank()) {
                icd.setDescription(description.get());
            }

            session.merge(icd);
            tx.commit();

            this.logger.info("ICD updated successfully");
            return icd;
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
        this.logger.info("Deleting ICD with id " + id);
        if (id < 1) {
            this.logger.severe("ICD id is invalid");
            throw new BadRequestException("invalid_id");
        }

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Icd icd = session.get(Icd.class, id);
            if (icd == null) {
                this.logger.severe("Could not find ICD with id " + id);
                throw new NotFoundException("icd_not_found");
            }

            session.delete(icd);
            tx.commit();
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

    public List<IcdOccurrenceDTO> mostFrequentIcds(int limit) throws RuntimeException {
        this.logger.info("Most Frequent ICDs for limit " + limit);

        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);

            Query query = session.createNativeQuery(
                "SELECT i.id, i.code, COUNT(d) AS count " +
                "FROM diagnosis d " +
                "LEFT JOIN icd i ON d.icd_id = i.id " +
                "GROUP BY i.id " +
                "ORDER BY COUNT(d) DESC " +
                "LIMIT :limit"
            ).setParameter("limit", limit);

            List<Object[]> list = query.getResultList();
            List<IcdOccurrenceDTO> dtos = new ArrayList<>();
            for (Object[] row : list) {
                IcdOccurrenceDTO dto = new IcdOccurrenceDTO();
                dto.setIcdId((Integer) row[0]);
                dto.setIcdCode((String) row[1]);
                dto.setOccurrence(Math.toIntExact((Long) row[2]));
                dtos.add(dto);
            }

            tx.commit();

            this.logger.info("Found most Frequent ICDs for limit " + limit);
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
