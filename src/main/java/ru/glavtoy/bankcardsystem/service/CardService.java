package ru.glavtoy.bankcardsystem.service;

import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public List<CardDTO> getAllCards() {
        return cardRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CardDTO> getCardsByOwner(String owner) {
        return cardRepository.findByOwnerUsernameContainingIgnoreCase(owner).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CardDTO getCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена!"));
        return toDto(card);
    }

    public CardDTO createCard(CardDTO cardDTO) {
        User owner = userRepository.findByUsername(cardDTO.getOwner())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));

        Card card = new Card();
        card.setNumber(cardDTO.getNumber());
        card.setExpiryDate(cardDTO.getExpiryDate());
        card.setOwner(owner);
        card.setStatus(Card.Status.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        return toDto(cardRepository.save(card));
    }

    public CardDTO updateStatus(Long id, Card.Status status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена!"));
        card.setStatus(status);
        return toDto(cardRepository.save(card));
    }

    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException("Карта не найдена!");
        }
        cardRepository.deleteById(id);
    }

    @Transactional
    public void transfer(TransferRequest request) {
        Card from = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new NotFoundException("Карта отправителя не найдена!"));
        Card to = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new NotFoundException("Карта получателя не найдена!"));

        if (from.getBalance().compareTo(request.getAmount()) < 0)
            throw new IllegalArgumentException("Недостаточный баланс");

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
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