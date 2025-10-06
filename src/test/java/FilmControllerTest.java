/*import com.fasterxml.jackson.databind.ObjectMapper;
import filmorate.FilmorateApplication;
import filmorate.model.Film;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    /*@Test
    void postFilms_success() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidFilm())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void postFilms_emptyName_shouldFail() throws Exception {
        Film film = createValidFilm();
        film.setName("");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postFilms_nullName_shouldFail() throws Exception {
        Film film = createValidFilm();
        film.setName(null);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postFilms_tooLongDescription_shouldFail() throws Exception {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postFilms_releaseDateTooEarly_shouldFail() throws Exception {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postFilms_negativeDuration_shouldFail() throws Exception {
        Film film = createValidFilm();
        film.setDuration(-1);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postFilms_zeroDuration_shouldFail() throws Exception {
        Film film = createValidFilm();
        film.setDuration(0);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateFilm_success() throws Exception {
        String jsonFilm = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidFilm())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Film added = objectMapper.readValue(jsonFilm, Film.class);
        added.setName("Updated");
        added.setDescription("New description");
        added.setReleaseDate(LocalDate.of(2010, 5, 5));
        added.setDuration(150);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(added)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }


    @Test
    void updateFilm_nonExistentId_shouldFail() throws Exception {
        Film updated = createValidFilm();
        updated.setId(999);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFilm_invalidName_shouldFail() throws Exception {
        String jsonFilm = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidFilm())))
                .andReturn().getResponse().getContentAsString();

        Film added = objectMapper.readValue(jsonFilm, Film.class);
        added.setName("");

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(added)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilm_tooLongDescription_shouldFail() throws Exception {
        String jsonFilm = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidFilm())))
                .andReturn().getResponse().getContentAsString();

        Film added = objectMapper.readValue(jsonFilm, Film.class);
        added.setDescription("a".repeat(201));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(added)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilm_releaseDateTooEarly_shouldFail() throws Exception {
        String jsonFilm = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidFilm())))
                .andReturn().getResponse().getContentAsString();

        Film added = objectMapper.readValue(jsonFilm, Film.class);
        added.setReleaseDate(LocalDate.of(1800, 1, 1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(added)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateFilm_invalidDuration_shouldFail() throws Exception {
        String jsonFilm = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidFilm())))
                .andReturn().getResponse().getContentAsString();

        Film added = objectMapper.readValue(jsonFilm, Film.class);
        added.setDuration(0);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(added)))
                .andExpect(status().isBadRequest());
    }
}*/
