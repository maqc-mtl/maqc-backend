package com.maqc.backend.repository;

import com.maqc.backend.model.Transaction;
import com.maqc.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByStripePaymentIntentId(String stripePaymentIntentId);
}
