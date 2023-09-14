package xyz.krsh.insecuresite.rest.entities.mongodb;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardgameDocument {

    @Field
    @Id
    private String id;

    private float price;

    private int quantity;

    private String description;

    public BoardgameDocument(String name) {
        this.id = name;
    }

}
