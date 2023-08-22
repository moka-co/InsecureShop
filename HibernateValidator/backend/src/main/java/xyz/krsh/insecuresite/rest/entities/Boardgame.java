package xyz.krsh.insecuresite.rest.entities;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import xyz.krsh.insecuresite.security.customConstraints.NoHtml;

/* 
    This class represents a boardgame.
    A board 
    - must have a name
    - could have a price, a quantity and a description
*/

@Entity
@Table(name = "Boardgame")
public class Boardgame {

    @Size(max = 1000)
    @NotNull
    @Id
    private String name;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column
    private float price = 0.0f;

    @Min(0)
    @Max(100)
    @Column
    private int quantity = 0;

    @NotNull
    @Size(max = 1025)
    @Column
    @NoHtml
    private String description = "";

    public Boardgame() { // Required by JPA

    }

    public Boardgame(String name) {
        this.name = "";
        if (name != null) {
            this.name = name;
        }
        this.description = "";
    }

    public Boardgame(String name, float price, int quantity, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    public String getId() {
        return this.getName();
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setPrice(float p) {
        this.price = p;
    }

    public void setQuantity(int q) {
        this.quantity = q;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String toString() {
        return "Boardgame{" + "name(id)=" + name +
                ", price: " + price +
                ", quantity: " + quantity +
                ", description: " + description +
                "}  ";
    }

}
