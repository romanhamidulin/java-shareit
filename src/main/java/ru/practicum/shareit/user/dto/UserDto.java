package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class UserDto {

    private Long id;
    @Length(min = 1, max = 30)
    private String name;
    @Email
    private String email;

}
