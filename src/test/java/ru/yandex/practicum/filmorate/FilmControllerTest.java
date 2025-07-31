package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FilmControllerTest {
    Gson gson;

    public FilmControllerTest() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Test
    void get400StatusIfEmptyDuration() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        String stringFilm = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringFilm))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void get400StatusIfNegativeDuration() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        film.setDuration(-1);
        String stringFilm = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringFilm))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void get200StatusWhenCreateFilm() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        film.setDuration(115);
        String stringFilm = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringFilm))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }


    @Test
    void get400StatusWhenCreateFilmWithEmptyName() throws IOException, InterruptedException {
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        film.setDuration(115);
        String stringFilm = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringFilm))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void get500StatusWhenCreateFilmWithEarlyReleaseDate() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test old Release Date");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1784, 10, 2));
        film.setDuration(115);
        String stringFilm = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringFilm))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    void getFilmAllFields() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        film.setDuration(115);
        String stringFilm = gson.toJson(film);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest requestToPost = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringFilm))
                .header("Content-Type", "application/json")
                .build();

        client.send(requestToPost, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Film> filmsFromResponse = gson.fromJson(response.body(), new FilmsListTypeToken().getType());
        assertEquals("Test", filmsFromResponse.get(0).getName(), "Имя фильма должно быть Test");
        assertEquals(115, filmsFromResponse.get(0).getDuration(), "Продолжительность фильма должно быть 115 минут");
        assertEquals("Description", filmsFromResponse.get(0).getDescription(), "Описание фильма должно быть Description");
        assertEquals("2024-10-02", filmsFromResponse.get(0).getReleaseDate().toString(), "Дата релиза фильма должно быть 2024-10-02");
    }

}

class FilmsListTypeToken extends TypeToken<List<Film>> {
}