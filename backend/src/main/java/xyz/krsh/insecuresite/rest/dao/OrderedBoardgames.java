package xyz.krsh.insecuresite.rest.dao;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/*
 * Represent Many-To-Many Relationship between Orders and Boardgames
 * with attribute quantity
 * OrderedBoardgamesId is an attribute
 */
@Entity
public class OrderedBoardgames {

    @EmbeddedId
    private OrderedBoardgamesId id = new OrderedBoardgamesId();

    @ManyToOne
    @MapsId("orderId")
    private Order order;

    @ManyToOne
    @MapsId("boardgameName")
    private Boardgame boardgame;

    private int quantity;

    public OrderedBoardgames() {

    }

    public OrderedBoardgames(OrderedBoardgamesId id, Order order, Boardgame boardgame, int quantity) {
        this.id = id;
        this.order = order;
        this.boardgame = boardgame;
        this.quantity = quantity;

    }

    public OrderedBoardgamesId getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Boardgame getBoardgame() {
        return boardgame;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOrderedBoardgamesId(OrderedBoardgamesId id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setBoardgame(Boardgame boardgame) {
        this.boardgame = boardgame;
    }

    public void increaseQuantity() {
        quantity += 1;
    }

    public void decreseQuantity() {
        quantity -= 1;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
