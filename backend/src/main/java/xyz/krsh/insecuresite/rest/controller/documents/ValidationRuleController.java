package xyz.krsh.insecuresite.rest.controller.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@RestController
public class ValidationRuleController {

    protected static final Logger logger = LogManager.getLogger();
    protected static final Logger loggerTwo = LogManager.getLogger("File2");

    @Autowired
    private ESAPIValidatorService validator;

    @GetMapping("/api/document/test/")
    public boolean getStringValidationRule() {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "descriptionsome descriptionsome descriptionsome descriptionsome description");
        try {
            return validator.validateBean2(boardgameDto, "boardgame_v2");
        } catch (Exception e) {
            loggerTwo.warn("Invalid bean: " + boardgameDto.toString());
            return false;
        }

    }

}
