package ru.glavtoy.bankcardsystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.glavtoy.bankcardsystem.dto.CardDTO;
import ru.glavtoy.bankcardsystem.service.CardService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(CardControllerTest.TestConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardService cardService;

    @Test
    void getBalance_ShouldReturnBalance() throws Exception {
        Long cardId = 1L;
        BigDecimal balance = new BigDecimal("1500.75");

        CardDTO dto = CardDTO.builder()
                .id(cardId)
                .number("1234567812345678")
                .owner("testuser")
                .expiryDate(LocalDate.now().plusYears(2))
                .status("ACTIVE")
                .balance(balance)
                .build();

        when(cardService.getCard(cardId)).thenReturn(dto);

        mockMvc.perform(get("/api/cards/{id}/balance", cardId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1500.75));
    }

    @Test
    void getBalance_WhenCardNotFound_ShouldReturnNotFound() throws Exception {
        Long cardId = 999L;

        when(cardService.getCard(cardId))
                .thenThrow(new RuntimeException("Карта не найдена!"));

        mockMvc.perform(get("/api/cards/{id}/balance", cardId))
                .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CardService cardService() {
            return Mockito.mock(CardService.class);
        }
    }
}