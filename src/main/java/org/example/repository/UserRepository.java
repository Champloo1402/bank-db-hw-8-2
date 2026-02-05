package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.User;

public class UserRepository {

    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public void save(User user){
        em.persist(user);
    }

    public User findById(Long id){
        return em.find(User.class, id);
    }
}
