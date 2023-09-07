package xyz.krsh.insecuresite.rest.controller.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@RestController
public class ValidationRuleController {

    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    private ESAPIValidatorService validator;

    @GetMapping("/api/document/test/")
    public boolean getStringValidationRule() {
        BoardgameDto boardgameDto = new BoardgameDto("someValue", (float) 1.0, 2,
                "some descriptionsome descriptionsome descriptionsome descriptionsome descriptionsome description");
        return validator.validateBoardgame2(boardgameDto);
    }

}
