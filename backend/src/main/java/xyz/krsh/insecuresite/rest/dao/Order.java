package xyz.krsh.insecuresite.rest.dao;

import java.util.Date;
import javax.persistence.*;

import xyz.krsh.insecuresite.security.User;

@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private Date orderDate;

    private double price = 0.0;

    public Order() {
    }

    public int getId() {
        return orderId;
    }

    public User getUser() {
        return user;

    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getPrice() {
        return price;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrderDate(Date date) {
        orderDate = date;
    }
}
