import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.Validator;
import org.springframework.test.context.ContextConfiguration;

import xyz.krsh.insecuresite.InsecuresiteApplication;
import xyz.krsh.insecuresite.security.ESAPI.ESAPIEncoderWrapper;
import xyz.krsh.insecuresite.security.ESAPI.ESAPIValidatorWrapper;

@ContextConfiguration(classes = InsecuresiteApplication.class)
public class EsapiInputValidationTest {

    private static final Logger logger = LogManager.getLogger();

    private ESAPIValidatorWrapper validatorWrapper = new ESAPIValidatorWrapper();
    private ESAPIEncoderWrapper encoderWrapper = new ESAPIEncoderWrapper();

    @Test
    public void checkIfClassIsOk() {

        Validator validator = validatorWrapper.getValidator();
        assertNotNull(validator);
        Encoder encoder = ESAPIEncoderWrapper.getEncoder();
        assertNotNull(encoder);

        logger.info("Initial test passed for encoder and validator wrapper");

    }

    @Test
    public void testValidateDescription() {
        Validator validator = validatorWrapper.getValidator();
        String testString = "abcd1234/-',12zxc";
        String typeValidation = "Description";

        boolean result = validator.isValidInput("test validator", testString, typeValidation, 1024, false, true);
        assertTrue(result);

        logger.info("Test passed for random string with validator");

    }

}
