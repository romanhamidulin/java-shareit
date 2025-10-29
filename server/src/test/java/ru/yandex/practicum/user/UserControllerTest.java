package ru.yandex.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final UserDto testUserDto = UserDto.builder()
            .id(1L)
            .name("John Doe")
            .email("john@example.com")
            .build();

    @Test
    void getAllUsers_shouldReturnUsersList() throws Exception {
        // Given
        List<UserDto> users = List.of(testUserDto);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userService).getAllUsers();
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        // Given
        when(userService.getById(1L)).thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).getById(1L);
    }

    @Test
    void getById_whenUserNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(userService.getById(999L)).thenThrow(new NotFoundException("Пользователь с ID 999 - не существует!"));

        // When & Then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getById(999L);
    }

    @Test
    void addUser_shouldCreateUser() throws Exception {
        // Given
        UserDto newUserDto = UserDto.builder()
                .name("New User")
                .email("new@example.com")
                .build();

        UserDto createdUserDto = UserDto.builder()
                .id(1L)
                .name("New User")
                .email("new@example.com")
                .build();

        when(userService.addUser(any(UserDto.class))).thenReturn(createdUserDto);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@example.com"));

        verify(userService).addUser(any(UserDto.class));
    }

    @Test
    void updateUser_shouldUpdateUser() throws Exception {
        // Given
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userService.updateUser(any(UserDto.class), eq(1L))).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService).updateUser(any(UserDto.class), eq(1L));
    }

    @Test
    void updateUser_withPartialData_shouldUpdateOnlyProvidedFields() throws Exception {
        // Given - обновляем только имя
        UserDto partialUpdateDto = UserDto.builder()
                .name("Only Name Updated")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Only Name Updated")
                .email("original@example.com") // email остался прежним
                .build();

        when(userService.updateUser(any(UserDto.class), eq(1L))).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Only Name Updated"))
                .andExpect(jsonPath("$.email").value("original@example.com"));

        verify(userService).updateUser(any(UserDto.class), eq(1L));
    }

    @Test
    void updateUser_whenUserNotFound_shouldReturnNotFound() throws Exception {
        // Given
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userService.updateUser(any(UserDto.class), eq(999L)))
                .thenThrow(new NotFoundException("Пользователь с ID 999 - не существует!"));

        // When & Then
        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(any(UserDto.class), eq(999L));
    }

    @Test
    void deleteById_shouldDeleteUser() throws Exception {
        // Given
        doNothing().when(userService).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteById(1L);
    }

    @Test
    void deleteById_withNonExistentUser_shouldSucceed() throws Exception {
        // Given
        doNothing().when(userService).deleteById(999L);

        // When & Then
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isOk());

        verify(userService).deleteById(999L);
    }

}