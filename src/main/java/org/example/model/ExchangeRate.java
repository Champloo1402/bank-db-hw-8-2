package org.example.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Currency fromCurrency;
    @Enumerated(EnumType.STRING)
    private Currency toCurrency;
    @Column(precision = 19, scale = 6)
    private BigDecimal rate;

    public Long getId() {
        return id;
    }

    public ExchangeRate() {
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
