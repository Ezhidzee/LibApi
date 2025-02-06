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
public class PublisherDto {

    private Long id;

    @NotBlank(message = "Publisher name cannot be blank")
    @Size(max = 100, message = "Publisher name should not be greater than 100 symbols")
    private String name;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    private Set<Long> bookIds;
}
