package ru.practicum.shareit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ShareItTests {
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;

	@Test
	public void addAndGetUserById() {
		userService.addUser(new UserDto(null, "test1", "test1@mail.ru"));
		userService.addUser(new UserDto(null, "test2", "test2@mail.ru"));
		assertThat(userService.getById(1).getName()).isEqualTo("SecondUser");
	}

	@Test
	public void addAndGetUserByNegativeId() {
		userService.addUser(new UserDto(null, "test1", "test1@mail.ru"));
		userService.addUser(new UserDto(null, "test2", "test2@mail.ru"));
		Assertions.assertThatThrownBy(() -> userService.getById(3))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Пользователь с ID 3 - не существует!");
	}

	@Test
	public void testCreateItemAndGetHim() {
		userService.addUser(new UserDto(null, "test1", "test1@mail.ru"));
		userService.addUser(new UserDto(null, "test2", "test2@mail.ru"));
		ItemDto itemDrillFromSecondUser = ItemDto.builder()
				.name("Дрель со сверлом 20 мм")
				.description("Дрель с помощью которой вы сможете что-то сделать, берите кто хочет")
				.build();

		itemService.addItem(itemDrillFromSecondUser, 1L);
		ItemDto itemToCheck = itemService.getAllUsersItems(1L).get(0);
		assertThat(itemToCheck.getName()).isEqualTo("Дрель со сверлом 20 мм");
		assertThat(itemToCheck.getDescription()).isEqualTo("Дрель с помощью которой вы сможете что-то сделать, берите кто хочет");
	}

}
