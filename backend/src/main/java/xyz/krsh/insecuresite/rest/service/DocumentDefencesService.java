package xyz.krsh.insecuresite.rest.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@Service
public class DocumentDefencesService {

    protected static final Logger logger = LogManager.getLogger();

    @Value("${apiKey}")
    String apiKey;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void enableOrDisableDocument(boolean switchValue, String documentName, String adminKey) {
        if (adminKey.equals(apiKey) == false) {
            logger.warn("Incorret key: " + adminKey);
            return;
        }

        MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection("validationRuleDocument");
        Document document = mongoCollection.find(new Document("_id", documentName)).first();
        if (document == null || (boolean) document.get("enabled") == switchValue) {
            return;
        }

        Document update = new Document("$set", new Document("enabled", switchValue));
        mongoCollection.updateOne(Filters.eq("_id", documentName),
                update);
    }

}
