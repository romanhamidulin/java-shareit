package ru.yandex.practicum.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.user.UserClient;
import ru.yandex.practicum.user.UserController;
import ru.yandex.practicum.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Тесты UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    @DisplayName("Должен получить пользователя по ID")
    void shouldGetUserById() throws Exception {
        // given
        Long userId = 1L;
        String expectedResponse = """
            {
                "id": 1,
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        when(userClient.getUserById(userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен получить всех пользователей")
    void shouldGetAllUsers() throws Exception {
        // given
        String expectedResponse = """
            [{
                "id": 1,
                "name": "John Doe",
                "email": "john@example.com"
            }, {
                "id": 2,
                "name": "Jane Smith",
                "email": "jane@example.com"
            }]
            """;

        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен создать нового пользователя")
    void shouldAddUser() throws Exception {
        // given
        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("new@example.com")
                .build();

        String expectedResponse = """
            {
                "id": 1,
                "name": "New User",
                "email": "new@example.com"
            }
            """;

        when(userClient.addUser(any(UserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse));

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void shouldUpdateUser() throws Exception {
        // given
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        String expectedResponse = """
            {
                "id": 1,
                "name": "Updated User",
                "email": "updated@example.com"
            }
            """;

        when(userClient.updateUser(eq(userId), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

}