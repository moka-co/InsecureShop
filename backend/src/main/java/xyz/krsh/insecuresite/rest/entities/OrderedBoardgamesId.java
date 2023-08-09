package xyz.krsh.insecuresite.rest.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class OrderedBoardgamesId implements Serializable {

    private static final long serialVersionUID = 1L;

    private int orderId;
    private String boardgameName;

    public OrderedBoardgamesId() {

    }

    public OrderedBoardgamesId(int id, String name) {
        orderId = id;
        boardgameName = name;
    }

    public void setOrderId(int id) {
        orderId = id;
    }

    public void setBoardgameName(String name) {
        boardgameName = name;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getBordgameName() {
        return boardgameName;
    }

}
