package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserControllerTest {
    Gson gson;

    public UserControllerTest() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Test
    void get400StatusIfEmptyEmail() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1994, 10, 2));
        String stringUser = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringUser))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void get400StatusIfWrongEmail() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("wrongEmail");
        user.setBirthday(LocalDate.of(1994, 10, 2));
        String stringUser = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringUser))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void get500StatusIfWrongLogin() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login with spaces");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(1994, 10, 2));
        String stringUser = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringUser))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    void get400StatusIfFutureBirthday() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(2025, 10, 2));
        String stringUser = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringUser))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void get200StatusWhenCreateUser() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(2025, 5, 2));
        String stringUser = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringUser))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void getUserAllFields() throws IOException, InterruptedException {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.of(2025, 5, 2));
        String stringUser = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest requestToPost = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringUser))
                .header("Content-Type", "application/json")
                .build();
        client.send(requestToPost, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<User> usersFromResponse = gson.fromJson(response.body(), new UsersListTypeToken().getType());

        assertNotNull(usersFromResponse, "Должен вернуться список пользователей");
        assertEquals("Test", usersFromResponse.get(0).getName(), "Имя пользователя должно быть Test");
        assertEquals("login", usersFromResponse.get(0).getLogin(), "Логин пользователя должно быть login");
        assertEquals("email@mail.ru", usersFromResponse.get(0).getEmail(), "Email пользователя должно быть email@mail.ru");
        assertEquals("2025-05-02", usersFromResponse.get(0).getBirthday().toString(), "Дата рождения пользователя должна быть 2025-05-02");
    }
}

