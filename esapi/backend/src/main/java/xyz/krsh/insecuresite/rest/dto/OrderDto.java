package xyz.krsh.insecuresite.rest.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.krsh.insecuresite.rest.entities.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    @NotNull
    @Min(1)
    @Max(Integer.MAX_VALUE - 1)
    private int orderId;

    @NotNull
    private User user;

    @NotNull
    private String orderDate;

}
