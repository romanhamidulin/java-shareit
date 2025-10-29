package ru.yandex.practicum.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    @Length(min = 1, max = 30)
    private String name;
    @Email
    private String email;

}