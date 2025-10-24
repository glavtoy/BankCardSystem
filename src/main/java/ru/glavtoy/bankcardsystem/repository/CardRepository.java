package ru.glavtoy.bankcardsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.glavtoy.bankcardsystem.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwnerUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<Card> findByOwnerUsername(String username, Pageable pageable);
}
