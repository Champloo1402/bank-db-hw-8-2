package org.example;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.Account;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.model.User;
import org.example.repository.AccountRepository;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.CurrencyConversionService;
import org.example.service.DepositService;
import org.example.service.TransferService;
import org.example.service.UserFundsService;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");
        EntityManager em = emf.createEntityManager();

        UserRepository userRepo = new UserRepository(em);
        AccountRepository accountRepo = new AccountRepository(em);
        TransactionRepository transactionRepo = new TransactionRepository(em);
        ExchangeRateRepository exchangeRateRepo = new ExchangeRateRepository(em);

        DepositService depositService = new DepositService(em, accountRepo, transactionRepo);
        TransferService transferService = new TransferService(em, accountRepo, transactionRepo);
        CurrencyConversionService conversionService = new CurrencyConversionService(em, accountRepo, transactionRepo, exchangeRateRepo);
        UserFundsService userFundsService = new UserFundsService(accountRepo, exchangeRateRepo);

        em.getTransaction().begin();
        User alex = new User();
        alex.setName("Alex");
        userRepo.save(alex);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Account accountUSD = new Account();
        accountUSD.setUser(alex);
        accountUSD.setCurrency(Currency.USD);
        accountUSD.setBalance(BigDecimal.valueOf(1000));
        accountRepo.save(accountUSD);

        Account accountUAH = new Account();
        accountUAH.setUser(alex);
        accountUAH.setCurrency(Currency.UAH);
        accountUAH.setBalance(BigDecimal.valueOf(5000));
        accountRepo.save(accountUAH);
        em.getTransaction().commit();

        System.out.println("Initial balances:");
        System.out.println("Alex USD: " + accountUSD.getBalance());
        System.out.println("Alex UAH: " + accountUAH.getBalance());

        em.getTransaction().begin();
        ExchangeRate usdToUah = new ExchangeRate();
        usdToUah.setFromCurrency(Currency.USD);
        usdToUah.setToCurrency(Currency.UAH);
        usdToUah.setRate(BigDecimal.valueOf(37)); // example rate
        exchangeRateRepo.save(usdToUah);
        em.getTransaction().commit();

        depositService.deposit(accountUSD.getId(), BigDecimal.valueOf(500), Currency.USD);
        depositService.deposit(accountUAH.getId(), BigDecimal.valueOf(2000), Currency.UAH);

        System.out.println("After deposits:");
        System.out.println("Alex USD: " + accountUSD.getBalance());
        System.out.println("Alex UAH: " + accountUAH.getBalance());

        em.getTransaction().begin();
        User bob = new User();
        bob.setName("Bob");
        userRepo.save(bob);

        Account bobUAH = new Account();
        bobUAH.setUser(bob);
        bobUAH.setCurrency(Currency.UAH);
        bobUAH.setBalance(BigDecimal.valueOf(1000));
        accountRepo.save(bobUAH);
        em.getTransaction().commit();

        transferService.transfer(accountUAH.getId(), bobUAH.getId(), BigDecimal.valueOf(1500), Currency.UAH);

        System.out.println("After transfer to Bob:");
        System.out.println("Alex UAH: " + accountUAH.getBalance());
        System.out.println("Bob UAH: " + bobUAH.getBalance());

        conversionService.convert(accountUSD.getId(), accountUAH.getId(), BigDecimal.valueOf(200));

        System.out.println("After conversion USD -> UAH:");
        System.out.println("Alex USD: " + accountUSD.getBalance());
        System.out.println("Alex UAH: " + accountUAH.getBalance());

        BigDecimal totalAlexUAH = userFundsService.getTotalFundsInUAH(alex.getId());
        System.out.println("Alex total funds in UAH: " + totalAlexUAH);

        em.close();
        emf.close();
    }
}