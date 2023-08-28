package xyz.krsh.insecuresite.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDB {
    @Autowired
    private MongoTemplate mongoTemplate;

}
