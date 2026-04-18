package com.medrec.persistence.specialty;

import com.medrec.dtos.CreateSpecialtyDTO;
import com.medrec.exception_handling.exceptions.*;
import com.medrec.grpc.users.Users;
import com.medrec.persistence.DBUtils;
import com.medrec.persistence.ICrudRepository;
import jakarta.persistence.EntityExistsException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SpecialtyRepository implements ICrudRepository<Specialty, CreateSpecialtyDTO, Users.UpdateSpecialtyRequest> {
    private static SpecialtyRepository instance;

    private final Logger logger = Logger.getLogger(SpecialtyRepository.class.getName());

    private SpecialtyRepository() {}

    public static SpecialtyRepository getInstance() {
        if (instance == null) {
            instance = new SpecialtyRepository();
        }

        return instance;
    }

    @Override
    public Specialty save(CreateSpecialtyDTO dto) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Specialty specialty = dto.getDomainModel();
            session.persist(specialty);
            tx.commit();

            this.logger.info("Specialty saved successfully");
            return specialty;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
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
    public Specialty findById(int id) throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Specialty specialty = session.get(Specialty.class, id);
            tx.commit();

            if (specialty == null) {
                throw new NotFoundException("specialty_not_found");
            }

            this.logger.info(String.format("Specialty with id %s found", id));
            return specialty;
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Specialty with id + " + id + " not found: " + e.getMessage());
            throw e;
        } catch (HibernateException e) {
            DBUtils.rollback(tx);
            this.logger.severe("Database exception found: " + e.getMessage());
            throw new DatabaseException("Database exception found");
        }
    }

    @Override
    public List<Specialty> findAll() throws RuntimeException {
        Transaction tx = null;
        try {
            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            List<Specialty> specialties = session.createQuery("from Specialty", Specialty.class).getResultList();
            tx.commit();
            return specialties;
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
    public Specialty update(Users.UpdateSpecialtyRequest request) throws RuntimeException {
        int id = request.getId();
        Optional<Specialty> specialtyOptional = Optional.ofNullable(findById(id));

        Transaction tx = null;
        try {
            if (specialtyOptional.isEmpty()) {
                throw new NotFoundException("specialty_not_found");
            }

            Specialty specialty = specialtyOptional.get();

            if (request.hasSpecialtyName()) {
                specialty.setSpecialtyName(request.getSpecialtyName());
            }

            if (request.hasSpecialtyDescription()) {
                specialty.setSpecialtyDescription(request.getSpecialtyDescription());
            }

            Session session = DBUtils.getCurrentSession();
            tx = DBUtils.getTransactionForSession(session);
            Specialty updatedSpecialty = session.merge(specialty);
            tx.commit();

            this.logger.info("Specialty updated successfully");
            return updatedSpecialty;
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
            Specialty specialty = session.get(Specialty.class, id);

            if (specialty == null) {
                throw new NotFoundException("specialty_not_found");
            }

            session.remove(specialty);
            tx.commit();

            this.logger.info(String.format("Specialty with id %s deleted successfully", id));
        } catch (ExceptionInInitializerError e) {
            DBUtils.rollback(tx);
            this.logger.severe("Exception found in database connection initialization: " + e.getMessage());
            throw new DatabaseConnectionException("Exception found in database connection initialization!");
        } catch (NotFoundException e) {
            DBUtils.rollback(tx);
            this.logger.severe(String.format("Specialty with id %s not found", id));
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
}
