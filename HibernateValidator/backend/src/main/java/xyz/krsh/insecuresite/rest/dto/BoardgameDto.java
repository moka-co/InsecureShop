package xyz.krsh.insecuresite.rest.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.krsh.insecuresite.security.HibernateValidator.customConstraints.NoHtml;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class BoardgameDto {

    @NotNull
    @NonNull
    @Size(max = 1024, message = "Exceeded max acceppted name length")
    private String name;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private float price;

    @Min(value = 0, message = "Must be greater or equal of zero")
    @Max(value = 100, message = "Must be lesser than 100")
    private int quantity;

    @Size(max = 1025)
    @Pattern(regexp = "^[a-zA-Z0-9 ._%'\"+-<>,;:/()]+$", message = "Characters not allowed")
    @NoHtml(message = "HTML is not acceptable input")
    private String description;

}
