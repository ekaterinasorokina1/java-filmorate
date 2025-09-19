package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTest {

    @Autowired
    protected UserService userService;
    @Autowired
    protected JdbcTemplate jdbc;

    @BeforeEach
    void beforeEach() {
        jdbc.update("DELETE FROM users");
        jdbc.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @Test
    public void testCreateNewUser() {
        UserDto userDto = createUser("1");
        assertThat(userDto)
                .hasFieldOrPropertyWithValue("name", "1test")
                .hasFieldOrPropertyWithValue("email", "1test@mail.ru")
                .hasFieldOrPropertyWithValue("login", "1testLogin")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2025, 5, 4));
    }

    @Test
    public void testFindUserById() {
        createUser("1");
        UserDto userDto = userService.getUserById(1);
        assertThat(userDto).hasFieldOrPropertyWithValue("id", 1);
    }


    @Test
    public void testGetAll() {
        createUser("1");
        List<UserDto> users = userService.getUsers();
        assertThat(users).asList().size().isEqualTo(1);
    }

    @Test
    public void testAddFriend() {
        UserDto userDto1 = createUser("1");
        UserDto userDto2 = createUser("2");
        userService.setFriend(userDto1.getId(), userDto2.getId());

        List<UserDto> friendsOfFirst = userService.getFriends(userDto1.getId());
        assertThat(friendsOfFirst).asList().size().isEqualTo(1);

        List<UserDto> friendsOfSeconds = userService.getFriends(userDto2.getId());
        assertThat(friendsOfSeconds).asList().size().isEqualTo(0);
    }

    @Test
    public void testDeleteFriend() {
        UserDto userDto1 = createUser("1");
        UserDto userDto2 = createUser("2");
        userService.setFriend(userDto1.getId(), userDto2.getId());

        List<UserDto> friendsOfFirst = userService.getFriends(userDto1.getId());
        assertThat(friendsOfFirst).asList().size().isEqualTo(1);

        userService.setFriend(userDto1.getId(), userDto2.getId());

        userService.deleteFriend(userDto1.getId(), userDto2.getId());
        List<UserDto> friendsOfFirstAfterDeleting = userService.getFriends(userDto1.getId());
        assertThat(friendsOfFirstAfterDeleting).asList().size().isEqualTo(0);
    }

    @Test
    public void testGetCommonFriends() {
        UserDto userDto1 = createUser("1");
        UserDto userDto2 = createUser("2");
        UserDto userDto3 = createUser("3");
        userService.setFriend(userDto1.getId(), userDto2.getId());
        userService.setFriend(userDto3.getId(), userDto2.getId());

        List<UserDto> friendsCommon = userService.getCommonFriend(userDto1.getId(), userDto3.getId());
        assertThat(friendsCommon).asList().size().isEqualTo(1);
    }


    private UserDto createUser(String id) {
        NewUserRequest userTestRequest = new NewUserRequest();
        userTestRequest.setEmail(id + "test@mail.ru");
        userTestRequest.setName(id + "test");
        userTestRequest.setLogin(id + "testLogin");
        userTestRequest.setBirthday(LocalDate.of(2025, 5, 4));
        return userService.createUser(userTestRequest);
    }
}
