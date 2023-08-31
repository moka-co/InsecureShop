import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.ValidationRule;
import org.owasp.esapi.Validator;
import org.owasp.esapi.reference.validation.IntegerValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ESAPI.CustomValidationRule;
import xyz.krsh.insecuresite.InsecuresiteApplication;
import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;
import xyz.krsh.insecuresite.rest.repository.mongodb.ValidationRuleRepository;
import xyz.krsh.insecuresite.rest.service.documents.ESAPIValidatorService;
import xyz.krsh.insecuresite.security.ESAPI.ESAPIEncoderWrapper;

@DataMongoTest
@ContextConfiguration(classes = InsecuresiteApplication.class)
public class ESAPICustomInputValidationTest {

    private static final Logger logger = LogManager.getLogger();

    private ESAPIValidatorService validatorWrapper = new ESAPIValidatorService();
    private ESAPIEncoderWrapper encoderWrapper = new ESAPIEncoderWrapper();

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoCollection<Document> mongoCollection;

    @Autowired
    private ValidationRuleRepository validationRuleRepository;

    @Before
    @Autowired
    public void setUp() {
        this.mongoCollection = this.mongoTemplate.getCollection("testValidationDocument");
    }

    // @Test
    public void testCustomValidationRuleImplementationBuilder() {
        mongoCollection = mongoTemplate.getCollection("testValidationDocument");
        assertNotNull("Assert MongoTemplate not nulla", mongoCollection);

        // Get from database doc with validation rule
        FindIterable<Document> query = mongoCollection.find(new Document("_id", "test"));
        Document doc = query.first();

        // Assemble CustomValidationRule
        // Get validation rule from document
        assertNotNull(doc.get("rule"));
        String regex = (String) doc.get("rule");
        logger.info("Regex got from database: " + regex);

        // Test is valid rule
        boolean testRegex = regex.startsWith("^") == false || regex.endsWith("}$") == false;
        assertFalse("Must be false or regex is wrong", testRegex);

        // Initialize CustomValidationRule and test two simple cases
        CustomValidationRule customRule = new CustomValidationRule("Test with database", encoderWrapper.getEncoder(),
                false, regex);
        String input = "ABCdef";
        boolean resultOne = customRule.isValid(input);

        String inputTwo = "ABCDef123";
        boolean resultTwo = customRule.isValid(inputTwo);

        assertTrue(resultOne);
        assertFalse(resultTwo);
        logger.info("workflow input test done");

    }

    @Test
    public void IntegerValidationRuleWithMongoDB() {
        mongoCollection = mongoTemplate.getCollection("testValidationDocument");
        Document doc = mongoCollection.find(new Document("_id", "test")).first();
        assertNotNull(doc);

        String typename = "Quantity Validation";
        Encoder encoder = encoderWrapper.getEncoder();
        int min = (int) doc.get("min");
        int max = (int) doc.get("max");

        ValidationRule integerRule = new IntegerValidationRule(typename, encoder, min, max);
        boolean result = integerRule.isValid("Test = -11", "-11");
        assertFalse(result);

        // Validator validator = validatorWrapper.getValidator();
        // validator.addRule(integerRule);

    }

    @Test
    public void checkValidationRuleDocument() {
        assertNotNull(validationRuleRepository);

        ValidationRuleDocument inserted = validationRuleRepository.save(new ValidationRuleDocument("test", "^[]{1,}$"));
        assertTrue("id is 'test'", inserted.getId().equals("test"));
        assertTrue("rule is '^[]{1,}$'", inserted.getRule().equals("^[]{1,}$"));

        inserted = validationRuleRepository.save(new ValidationRuleDocument("jsessionid", "^[A-Za-z0-9-_]{10,50}$"));
    }

}
