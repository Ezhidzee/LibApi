package su.ezhidze.libapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name should not be greater than 100 symbols")
    private String name;

    @Size(max = 1000, message = "Biography should not be greater than 1000 symbols")
    private String biography;

    private Set<Long> bookIds;
}
