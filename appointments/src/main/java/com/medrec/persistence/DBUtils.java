package com.medrec.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.logging.Logger;

public class DBUtils {
    private static final SessionFactory sessionFactory;

    static {
        Logger logger = Logger.getLogger(DBUtils.class.getName());

        try {
            Configuration configuration = new Configuration();

            configuration.setProperty("hibernate.connection.url", System.getenv("DB_URL"));
            configuration.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
            configuration.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));

            sessionFactory = configuration.configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (HibernateException e) {
            logger.severe("Initial SessionFactory creation failed: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Session getCurrentSession() throws HibernateException {
        return sessionFactory.getCurrentSession();
    }

    public static Transaction getTransactionForSession(Session session) throws HibernateException {
        Transaction transaction = session.getTransaction();
        if (transaction.isActive()) {
            return transaction;
        }
        return getCurrentSession().beginTransaction();
    }

    public static void shutdown() throws HibernateException {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
