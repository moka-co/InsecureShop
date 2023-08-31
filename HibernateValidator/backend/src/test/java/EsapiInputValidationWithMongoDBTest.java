import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import ESAPI.CustomValidationRule;
import xyz.krsh.insecuresite.InsecuresiteApplication;
import xyz.krsh.insecuresite.security.ESAPI.ESAPIEncoderWrapper;
import xyz.krsh.insecuresite.security.ESAPI.ESAPIValidatorWrapper;

@DataMongoTest
@ContextConfiguration(classes = InsecuresiteApplication.class)
public class EsapiInputValidationWithMongoDBTest {

    private static final Logger logger = LogManager.getLogger();

    // private ESAPIValidatorWrapper validatorWrapper = new ESAPIValidatorWrapper();
    private ESAPIEncoderWrapper encoderWrapper = new ESAPIEncoderWrapper();

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoCollection<Document> mongoCollection;

    @Test
    public void testCustomValidationRuleImplementationBuilder() {
        mongoCollection = mongoTemplate.getCollection("testValidationDocument");
        assertNotNull("Assert MongoTemplate not nulla", mongoCollection);

        // Get from database doc
        FindIterable<Document> query = mongoCollection.find(new Document("_id", "test"));
        Document doc = query.first();

        // Assemble CustomValidationRule
        assertNotNull(doc.get("rule"));
        String regex = (String) doc.get("rule");

        logger.info("Regex got from database: " + regex);
        boolean testRegex = regex.startsWith("^") == false || regex.endsWith("}$") == false;
        assertFalse("Must be false or regex is wrong", testRegex);

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

    // @Test
    public void checkRegexWorks() {
        String regex = "^[A-Za-z]{1,}$";
        String testString = "zxc";

        Pattern p = Pattern.compile(regex);
        System.out.println("Pattern p: " + p.toString());

        Matcher m = p.matcher(testString);
        System.out.println("Matches? " + m.matches());
        System.out.println("To match result " + m.toMatchResult().toString());

    }

}
