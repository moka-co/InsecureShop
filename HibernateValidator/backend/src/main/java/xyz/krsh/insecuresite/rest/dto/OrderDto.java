package xyz.krsh.insecuresite.rest.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.krsh.insecuresite.rest.entities.User;

@Getter
@Setter
@AllArgsConstructor
public class OrderDto {
    private int orderId;
    private User user;
    private Date orderDate;

}
