package xyz.krsh.insecuresite.rest.controller.documents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.service.DocumentDefencesService;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@RestController
public class ValidationRuleController {

    protected static final Logger logger = LogManager.getLogger();
    protected static final Logger loggerTwo = LogManager.getLogger("File2");

    @Autowired
    private ESAPIValidatorService validator;

    @Autowired
    private DocumentDefencesService defenceService;

    @GetMapping("/api/document/test/")
    public boolean getStringValidationRule() {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "descriptionsome descriptionsome descriptionsome descriptionsome description");
        try {
            loggerTwo.info("ValidationRuleController - Called /api/document/test");
            return validator.validateBean(boardgameDto, "boardgame_v2");
        } catch (Exception e) {
            loggerTwo.warn("ValidationRuleController: Invalid bean: " + boardgameDto.toString());
            return false;
        }

    }

    @PostMapping("/api/document/up/")
    public void defencesUp() {
        defenceService.enableOrDisableDocument(true, "boardgame_v2");

    }

    @PostMapping("/api/document/down/")
    public void defencesDown() {
        defenceService.enableOrDisableDocument(false, "boardgame_v2");
    }

}
