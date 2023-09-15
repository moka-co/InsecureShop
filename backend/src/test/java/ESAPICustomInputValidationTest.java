import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.ValidationRule;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.IntegerValidationRule;
import org.owasp.esapi.reference.validation.NumberValidationRule;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ESAPI.CustomValidationRule;
import xyz.krsh.insecuresite.InsecuresiteApplication;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;
import xyz.krsh.insecuresite.rest.repository.mongodb.ValidationRuleRepository;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@DataMongoTest
@ContextConfiguration(classes = InsecuresiteApplication.class)
public class ESAPICustomInputValidationTest {

    private static final Logger logger = LogManager.getLogger();

    private Encoder encoder = ESAPI.encoder();
    // private ESAPIEncoderWrapper encoderWrapper = new ESAPIEncoderWrapper();

    @Autowired
    private ESAPIValidatorService validator;

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
        CustomValidationRule customRule = new CustomValidationRule("Test with database", encoder,
                false, regex);
        String input = "ABCdef";
        boolean resultOne = customRule.isValid(input);

        String inputTwo = "ABCDef123";
        boolean resultTwo = customRule.isValid(inputTwo);

        assertTrue(resultOne);
        assertFalse(resultTwo);
        logger.info("workflow input test done");

    }

    // @Test
    public void IntegerValidationRuleWithMongoDB() {
        mongoCollection = mongoTemplate.getCollection("testValidationDocument");
        Document doc = mongoCollection.find(new Document("_id", "test")).first();
        assertNotNull(doc);

        String typename = "Quantity Validation";
        int min = (int) doc.get("min");
        int max = (int) doc.get("max");

        ValidationRule integerRule = new IntegerValidationRule(typename, encoder, min, max);
        boolean result = integerRule.isValid("Test = -11", "-11");
        assertFalse(result);

        // Validator validator = validatorWrapper.getValidator();
        // validator.addRule(integerRule);

    }

    // @Test
    public void checkValidationRuleDocument() {
        assertNotNull(validationRuleRepository);

        ValidationRuleDocument inserted = validationRuleRepository.save(new ValidationRuleDocument("test", "^[]{1,}$"));
        assertTrue("id is 'test'", inserted.getId().equals("test"));
        assertTrue("rule is '^[]{1,}$'", inserted.getRule().equals("^[]{1,}$"));

        inserted = validationRuleRepository.save(new ValidationRuleDocument("jsessionid", "^[A-Za-z0-9-_]{10,50}$"));
    }

    // @Test
    public void testBoardgameValidator() {
        mongoCollection = mongoTemplate.getCollection("validationRuleDocument");
        assertNotNull("Assert MongoTemplate not nulla", mongoCollection);

        // Get from database doc with validation rule
        FindIterable<Document> query = mongoCollection.find(new Document("_id", "boardgame"));
        Document doc = query.first();

        // Assemble CustomValidationRule
        // Get validation rule from document
        assertNotNull(doc.get("description"));
        String description = (String) doc.get("description");
        logger.info("Regex got from database: " + description);

        // Initialize CustomValidationRule and test two simple cases
        StringValidationRule stringRule = new StringValidationRule("Boardgame Description Validation",
                encoder, description);
        String input = "ABCdef";
        boolean resultOne = stringRule.isValid("Testing description input 1", input);

        String inputTwo = "ABCDef123![][]++";
        boolean resultTwo = stringRule.isValid("Testing description input 2", inputTwo);

        assertTrue(resultOne);
        assertFalse(resultTwo);
        logger.info("workflow input test done");

        Document doc2 = (Document) doc.get("quantity");
        int qMin = (int) doc2.get("min");
        int qMax = (int) doc2.get("max");

        int quantity = 12;
        NumberValidationRule quantityRule = new NumberValidationRule(inputTwo, encoder, qMin, qMax);

        assertTrue(quantityRule.isValid("Quantity 1 test valid", String.valueOf(quantity)));

        int quantity2 = 123456;

        assertFalse(quantityRule.isValid("Quantity 2 test invalid", String.valueOf(quantity2)));

        logger.info("Validation quantity test done");

    }

    @Test
    public void testFailValidator() throws ValidationException {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "<script>alert(1)</script>descriptionsome descriptionsome descriptionsome descriptionsome description");
        boolean result = validator.validateBean(boardgameDto, "boardgame_v2");
        assertFalse(result);

    }

}
