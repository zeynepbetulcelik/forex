package com.forexservice.repository;

import com.forexservice.data.entity.CurrencyConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, UUID> {
    Optional<CurrencyConversion> findById(UUID id);
    List<CurrencyConversion> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}