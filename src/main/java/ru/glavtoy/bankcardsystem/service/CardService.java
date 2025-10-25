package ru.glavtoy.bankcardsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glavtoy.bankcardsystem.dto.CardDTO;
import ru.glavtoy.bankcardsystem.dto.TransferRequest;
import ru.glavtoy.bankcardsystem.entity.Card;
import ru.glavtoy.bankcardsystem.entity.User;
import ru.glavtoy.bankcardsystem.exception.NotFoundException;
import ru.glavtoy.bankcardsystem.repository.CardRepository;
import ru.glavtoy.bankcardsystem.repository.UserRepository;
import ru.glavtoy.bankcardsystem.util.CardUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<CardDTO> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::toDtoWithExpiryCheck);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getCardsByOwner(String owner, Pageable pageable) {
        return cardRepository.findByOwnerUsernameContainingIgnoreCase(owner, pageable).map(this::toDtoWithExpiryCheck);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getMyCards(String username, Pageable pageable) {
        return cardRepository.findByOwnerUsername(username, pageable).map(this::toDtoWithExpiryCheck);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long cardId, String username) {
        return cardRepository.findById(cardId)
                .map(card -> card.getOwner().getUsername().equals(username))
                .orElse(false);
    }

    @Transactional
    public CardDTO getCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена!"));
        checkAndUpdateExpiry(card);
        return toDto(card);
    }

    @Transactional
    public CardDTO createCard(CardDTO cardDTO) {
        User owner = userRepository.findByUsername(cardDTO.getOwner())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));

        Card card = new Card();
        card.setNumber(cardDTO.getNumber().replaceAll("[\\s-]", ""));
        card.setExpiryDate(cardDTO.getExpiryDate());
        card.setOwner(owner);
        card.setStatus(Card.Status.ACTIVE);
        card.setBalance(cardDTO.getBalance() == null ? BigDecimal.ZERO : cardDTO.getBalance());

        Card saved = cardRepository.save(card);
        return toDto(saved);
    }

    @Transactional
    public CardDTO updateStatus(Long id, Card.Status status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена!"));
        checkAndUpdateExpiry(card);
        if (status == Card.Status.EXPIRED) {
            throw new IllegalArgumentException("Статус EXPIRED устанавливается автоматически");
        }
        card.setStatus(status);
        return toDto(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("Карта не найдена!");
        }
        cardRepository.deleteById(id);
    }

    @Transactional
    public void transfer(TransferRequest request) {
        if (request.getFromCardId().equals(request.getToCardId()))
            throw new IllegalArgumentException("Нельзя переводить на ту же карту");

        Card from = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new NotFoundException("Карта отправителя не найдена!"));
        checkAndUpdateExpiry(from);

        Card to = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new NotFoundException("Карта получателя не найдена!"));
        checkAndUpdateExpiry(to);

        if (from.getOwner() == null || to.getOwner() == null)
            throw new IllegalArgumentException("Невозможно определить владельцев карт");

        if (!from.getOwner().getUsername().equals(to.getOwner().getUsername()))
            throw new IllegalArgumentException("Перевод возможен только между картами одного владельца");

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");

        if (from.getBalance().compareTo(request.getAmount()) < 0)
            throw new IllegalArgumentException("Недостаточный баланс");

        if (from.getStatus() != Card.Status.ACTIVE || to.getStatus() != Card.Status.ACTIVE) {
            throw new IllegalArgumentException("Перевод возможен только если карта активна");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        cardRepository.save(from);
        cardRepository.save(to);
    }

    private void checkAndUpdateExpiry(Card card) {
        if (card.getExpiryDate().isBefore(LocalDate.now()) && card.getStatus() != Card.Status.EXPIRED) {
            card.setStatus(Card.Status.EXPIRED);
            cardRepository.save(card);
        }
    }

    private CardDTO toDtoWithExpiryCheck(Card card) {
        checkAndUpdateExpiry(card);
        return toDto(card);
    }

    private CardDTO toDto(Card card) {
        return CardDTO.builder()
                .id(card.getId())
                .number(CardUtil.maskCardNumber(card.getNumber()))
                .owner(card.getOwner().getUsername())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus().name())
                .balance(card.getBalance())
                .build();
    }
}