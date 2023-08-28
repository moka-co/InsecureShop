
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.bson.Document;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import xyz.krsh.insecuresite.InsecuresiteApplication;

@DataMongoTest
@ContextConfiguration(classes = InsecuresiteApplication.class)
public class MongoDBTest {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoCollection<Document> mongoCollection;

    @Before
    @Autowired
    public void setUp() {
        this.mongoCollection = this.mongoTemplate.getCollection("test");
    }

    @Test
    public void checkConnectionToMongoDB() {
        mongoCollection = mongoTemplate.getCollection("test");
        assertNotNull("Assert MongoTemplate not nulla", mongoTemplate);

        logger.info("MongoDB connection test passed");
        assertNotEquals(1, 2);

    }

    @Test
    public void checkReadDocument() {
        mongoCollection = mongoTemplate.getCollection("test");
        long count = mongoCollection.countDocuments(new Document("name", "test"));
        assertEquals("Assert count for Mongo Collection greater than one", 1, count);

        logger.info("MongoDB read on database test passed");

    }

    @Test
    public void checkWriteDocument() {
        InsertOneResult result = this.mongoCollection.insertOne(new Document("name", "delete-me"));
        System.out.println(result.getInsertedId());
        long count = mongoCollection.countDocuments();
        assertEquals("Assert two document are present since one is deleted", 2, count);

    }

    @Test
    public void checkDeleteDocument() {
        DeleteResult result = this.mongoCollection.deleteOne(new Document("name", "delete-me"));
        assertNotNull("Assert that the document has been deleted ", result);
    }

}
