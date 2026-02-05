package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Account;

import java.util.List;

public class AccountRepository {

    private final EntityManager em;

    public AccountRepository(EntityManager em) {
        this.em = em;
    }

    public List<Account> findByUserId(Long userId) {
        return em.createQuery("SELECT a FROM Account a WHERE a.user.id = :userId", Account.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void save(Account account) {
        em.persist(account);
    }

    public Account findById(Long id) {
        return em.find(Account.class, id);
    }
}
