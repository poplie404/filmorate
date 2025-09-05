package filmorate.model;

import filmorate.validation.ReleaseDateAfter;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    private Set<Integer> likes = new HashSet<>();

    @NotBlank(message = "Имя фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    @ReleaseDateAfter // кастомная аннотация, проверяющая дату >= 28.12.1895
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;
}
