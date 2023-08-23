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
import lombok.Setter;
import xyz.krsh.insecuresite.security.customConstraints.NoHtml;

@Getter
@Setter
@AllArgsConstructor
public class BoardgameDto {

    @Size(max = 1000)
    @NotNull
    private String name;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private float price;

    @Min(0)
    @Max(100)
    private int quantity;

    @Size(max = 1025)
    @NoHtml
    @Pattern(regexp = "^[a-zA-Z0-9._%'\"+-<>,;:/()]+$", message = "Characters not allowed")
    private String description;

}
