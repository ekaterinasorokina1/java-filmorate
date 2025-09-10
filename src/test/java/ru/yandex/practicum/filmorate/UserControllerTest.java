package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    public UserControllerTest() throws IOException {
    }

    @Autowired
    TestRestTemplate template;

    @Test
    void get400StatusIfEmptyEmail() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1994, 10, 2));

        ResponseEntity<User> entity = template.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void get400StatusIfWrongEmail() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("wrongEmail");
        user.setBirthday(LocalDate.of(1994, 10, 2));
        ResponseEntity<User> entity = template.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void get400StatusIfWrongLogin() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login with spaces");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(1994, 10, 2));
        ResponseEntity<User> entity = template.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void get400StatusIfFutureBirthday() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(2025, 10, 2));
        ResponseEntity<User> entity = template.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void getUserAllFields() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(2025, 5, 2));

        template.postForEntity("/users", user, User.class);
        ResponseEntity<User[]> entity = template.getForEntity("/users", User[].class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        User[] users = entity.getBody();

        assertNotNull(users, "Должен вернуться список пользователей");
        assertEquals("Test", users[0].getName(), "Имя пользователя должно быть Test");
        assertEquals("login", users[0].getLogin(), "Логин пользователя должно быть login");
        assertEquals("email@mail.ru", users[0].getEmail(), "Email пользователя должно быть email@mail.ru");
        assertEquals("2025-05-02", users[0].getBirthday().toString(), "Дата рождения пользователя должна быть 2025-05-02");
    }
}

