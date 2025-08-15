package filmorate.model;

import filmorate.validation.ReleaseDateAfter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Имя фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может быть более 200 символов")
    private String description;

    @ReleaseDateAfter
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;


}
