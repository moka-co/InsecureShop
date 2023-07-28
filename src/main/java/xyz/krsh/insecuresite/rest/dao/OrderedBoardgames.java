package xyz.krsh.insecuresite.rest.dao;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class OrderedBoardgames {

    @EmbeddedId
    private OrderedBoardgamesId id = new OrderedBoardgamesId(); // hai bisogno di una
    // Classe quando devi fare una tabella many to many con attributi
    // Questo perchè devi rappresentare un id campi multipli e quindi una classe

    @ManyToOne
    @MapsId("orderId")
    private Order order;

    @ManyToOne
    @MapsId("boardgameName") // TODO: commentare qui
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
