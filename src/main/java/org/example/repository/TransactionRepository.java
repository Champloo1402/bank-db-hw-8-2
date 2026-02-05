package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Transaction;

public class TransactionRepository {

    private final EntityManager em;

    public TransactionRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Transaction transaction) {
        em.persist(transaction);
    }

    public Transaction findById(Long id) {
        return em.find(Transaction.class, id);
    }
}
