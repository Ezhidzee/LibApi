package su.ezhidze.libapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title should not be greater than 100 symbols")
    private String title;

    @NotNull(message = "Publication year must be provided")
    private Integer publicationYear;

    @NotBlank(message = "ISBN cannot be blank")
    private String isbn;

    @NotNull(message = "Page count must be provided")
    private Integer pageCount;

    private Long publisherId;

    private Set<Long> authorIds;
}
