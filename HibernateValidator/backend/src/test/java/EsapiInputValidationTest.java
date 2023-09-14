import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.ValidationRule;
import org.owasp.esapi.Validator;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import ESAPI.CustomValidationRule;
import xyz.krsh.insecuresite.InsecuresiteApplication;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@DataMongoTest
@ContextConfiguration(classes = InsecuresiteApplication.class)
public class EsapiInputValidationTest {

    private static final Logger logger = LogManager.getLogger();

    private ESAPIValidatorService validatorWrapper = new ESAPIValidatorService();
    private Encoder encoder = ESAPI.encoder();

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoCollection<Document> mongoCollection;

    @Before
    public void initialize() {
        ESAPI.validator();
    }

    // @Test
    public void checkIfClassIsOk() {

        Validator validator = validatorWrapper.getValidator();
        assertNotNull(validator);
        assertNotNull(encoder);

        logger.info("Initial test passed for encoder and validator wrapper");

    }

    // @Test
    public void testValidateDescription() {
        Validator validator = validatorWrapper.getValidator();
        String testString = "abcd1234/-',12zxc";
        String typeValidation = "Description";

        boolean result = validator.isValidInput("test validator", testString, typeValidation, 1024, false, true);
        assertTrue(result);

        logger.info("Test passed for random string with validator");

    }

    // @Test
    public void testValidationRule() {
        Validator validator = validatorWrapper.getValidator();
        StringValidationRule testRule = new StringValidationRule("Test Rule", encoder,
                "A-Z");
        validator.addRule(testRule);

        ValidationRule getRule = validator.getRule("Test Rule");
        assertNotNull(getRule);

        logger.info("Validation Rule registrated with success");
    }

    @Test
    public void testStringValidationRule() {
        ValidationRule testRule = new StringValidationRule("Test rule", encoder, "^[A-Z]{1,}$");

        String test = "ABCDefghi";
        boolean result = testRule.isValid("Validating " + test + " with testRule", test);

        String testTwo = "ABC";
        boolean resultTwo = testRule.isValid("Validating " + testTwo + " with testRule ", testTwo);

        assertFalse("Result should be false", result);
        assertTrue("Should be true", resultTwo);
        logger.info(
                "Correctly validated test string 'ABCDefghi' (=assertFalse) and 'ABC'(=assertTrue) with whitelist A-Z");

    }

    // @Test
    public void testMyCustomValidationRuleImplementation() {
        CustomValidationRule customRule = new CustomValidationRule("My custom rule", encoder, false,
                "^[A-Z]{1,}$");
        String test = "ABCDefghi";
        boolean result = customRule.isValid("Validating " + test + " with customRule", test);
        assertFalse(result);
        logger.info("Validated string: " + test + " with pattern" + customRule.getPattern().toString());

        result = customRule.isValid("Validating check null", null);
        assertFalse(result);
        logger.info("Correctly validated null string isn't falid therefore result is false");

    }

    // @Test
    public void testCustomValidationRuleImplementationBuilder() {
        mongoCollection = mongoTemplate.getCollection("test");
        assertNotNull("Assert MongoTemplate not nulla", mongoCollection);

        // Get from database doc
        FindIterable<Document> query = mongoCollection.find(new Document("_id", "test"));
        Document doc = query.first();
        assertNotNull(doc);

        // Assemble CustomValidationRule
        assertNotNull(doc.get("rule"));
        String regex = (String) doc.get("rule");

        logger.info("Regex got from database: " + regex);

        CustomValidationRule customRule = new CustomValidationRule("Test with database", encoder,
                false, regex);
        String input = "ABCdef";
        boolean resultOne = customRule.isValid("Testing if " + input + " is valid", input);

        String inputTwo = "ABCDef123";
        boolean resultTwo = customRule.isValid("Testing if " + inputTwo + " is valid", inputTwo);

        assertTrue(resultOne);
        assertFalse(resultTwo);
        logger.info("workflow input test done");

    }

}
