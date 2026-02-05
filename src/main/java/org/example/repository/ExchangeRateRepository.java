package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Currency;
import org.example.model.ExchangeRate;

public class ExchangeRateRepository {

    private final EntityManager em;

    public ExchangeRateRepository(EntityManager em) {
        this.em = em;
    }

    public void save(ExchangeRate exchangeRate) {
        em.persist(exchangeRate);
    }

    public ExchangeRate findByFromAndToCurrency(Currency from, Currency to) {
        try {
            return em.createQuery(
                            "SELECT e FROM ExchangeRate e WHERE e.fromCurrency = :from AND e.toCurrency = :to",
                            ExchangeRate.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // no rate found
        }
    }
}
