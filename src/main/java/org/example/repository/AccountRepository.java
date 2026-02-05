package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Account;

public class AccountRepository {

    private final EntityManager em;

    public AccountRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Account account) {
        em.persist(account);
    }

    public Account findById(Long id) {
        return em.find(Account.class, id);
    }
}
