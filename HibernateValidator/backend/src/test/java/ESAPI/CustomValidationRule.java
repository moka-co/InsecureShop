package ESAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.BaseValidationRule;

public class CustomValidationRule extends BaseValidationRule {
    private static final Logger logger = LogManager.getLogger();

    protected List<Pattern> whitelistPatterns = new ArrayList<Pattern>();
    protected int maxLength = Integer.MAX_VALUE;

    public CustomValidationRule(String typeName, Encoder encoder) {
        super(typeName, encoder);
    }

    public CustomValidationRule(String typeName, Encoder encoder, boolean allowNull, String whitelistPattern) {
        super(typeName, encoder);
        this.allowNull = allowNull;
        addWhiteListPattern(whitelistPattern);
    }

    public void addWhiteListPattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        try {
            whitelistPatterns.add(Pattern.compile(pattern));
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Validation misconfiguration, problem with specific pattern: " + pattern,
                    e);
        }
    }

    @Override
    public Object getValid(String context, String input) throws ValidationException {
        if (allowNull == true && input == null) {
            throw new ValidationException("Invalid input", "Invalid input is null");
        }

        String canonicalizedInput = encoder.canonicalize(input);
        for (Pattern p : whitelistPatterns) {
            if (!p.matcher(input).matches()) {
                throw new ValidationException("Invalid input", "Invalid input " + input + " doesn't match " + p);
            }

        }

        return canonicalizedInput;
    }

    public boolean isValid(String input) {
        if (allowNull == true && input == null) {
            return false;
        }
        String canonicalizedInput = encoder.canonicalize(input);
        for (Pattern p : whitelistPatterns) {
            if (!p.matcher(input).matches()) {
                System.out.println(p);
                return false;
            }
        }

        return true;
    }

    @Override
    protected Object sanitize(String context, String input) {
        try {
            return this.getValid(context, input);
        } catch (ValidationException e) {
            return "";
        }
    }

    public List<Pattern> getPattern() {
        return whitelistPatterns;
    }
}
