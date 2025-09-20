package ru.glavtoy.bankcardsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.glavtoy.bankcardsystem.entity.Card;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByOwnerUsernameContainingIgnoreCase(String username);
}
