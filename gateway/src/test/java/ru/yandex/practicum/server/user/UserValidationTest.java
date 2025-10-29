package ru.yandex.practicum.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.user.UserClient;
import ru.yandex.practicum.user.UserController;
import ru.yandex.practicum.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Тесты валидации UserController")
class UserValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    @DisplayName("Должен вернуть 400 при создании пользователя с пустым именем")
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        // given
        UserDto invalidDto = UserDto.builder()
                .name("") // невалидно
                .email("test@example.com")
                .build();

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при создании пользователя с невалидным email")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // given
        UserDto invalidDto = UserDto.builder()
                .name("Test User")
                .email("invalid-email") // невалидно
                .build();

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при невалидном ID в пути")
    void shouldReturnBadRequestForInvalidPathId() throws Exception {
        // given
        Long invalidId = 0L; // @Positive требует > 0

        // when & then
        mockMvc.perform(get("/users/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при отрицательном ID")
    void shouldReturnBadRequestForNegativeId() throws Exception {
        // given
        Long negativeId = -1L; // @Positive требует > 0

        // when & then
        mockMvc.perform(get("/users/{id}", negativeId))
                .andExpect(status().isBadRequest());
    }
}