package ru.glavtoy.bankcardsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.glavtoy.bankcardsystem.dto.CardDTO;
import ru.glavtoy.bankcardsystem.entity.Card;
import ru.glavtoy.bankcardsystem.entity.User;
import ru.glavtoy.bankcardsystem.repository.CardRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_ShouldReturnCardDTO() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("testuser");

        Card card = new Card();
        card.setNumber("1234567812345678");
        card.setOwner(owner);
        card.setExpiryDate(LocalDate.now().plusYears(3));

        Card savedCard = new Card();
        savedCard.setId(1L);
        savedCard.setNumber("1234567812345678");
        savedCard.setOwner(owner);
        savedCard.setBalance(BigDecimal.ZERO);
        savedCard.setStatus(Card.Status.ACTIVE);
        savedCard.setExpiryDate(LocalDate.now().plusYears(3));

        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        CardDTO inputDto = CardDTO.builder()
                .number("1234567812345678")
                .owner("testuser")
                .expiryDate(LocalDate.now().plusYears(3))
                .build();

        CardDTO result = cardService.createCard(inputDto);

        assertNotNull(result);
        assertEquals("1234567812345678", result.getNumber());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void getCard_ShouldReturnCardDTO() {
        Long cardId = 1L;

        User owner = new User();
        owner.setUsername("testuser");

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("1234567812345678");
        card.setOwner(owner);
        card.setBalance(new BigDecimal("1000.50"));
        card.setStatus(Card.Status.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardDTO result = cardService.getCard(cardId);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals("1000.50", result.getBalance().toString());
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    void getCard_WhenCardNotFound_ShouldThrowException() {
        Long cardId = 999L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cardService.getCard(cardId));
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    void updateStatus_ShouldUpdateCardStatus() {
        Long cardId = 1L;

        User owner = new User();
        owner.setUsername("testuser");

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("1234567812345678");
        card.setOwner(owner);
        card.setStatus(Card.Status.ACTIVE);

        Card blockedCard = new Card();
        blockedCard.setId(cardId);
        blockedCard.setNumber("1234567812345678");
        blockedCard.setOwner(owner);
        blockedCard.setStatus(Card.Status.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(blockedCard);

        CardDTO result = cardService.updateStatus(cardId, Card.Status.BLOCKED);

        assertNotNull(result);
        assertEquals("BLOCKED", result.getStatus());
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardRepository, times(1)).save(any(Card.class));
    }
}