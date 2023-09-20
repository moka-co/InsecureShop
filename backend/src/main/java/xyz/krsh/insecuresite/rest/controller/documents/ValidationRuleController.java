package xyz.krsh.insecuresite.rest.controller.documents;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.service.DocumentDefencesService;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@RestController
public class ValidationRuleController {

    protected static final Logger logger = LogManager.getLogger();
    protected static final Logger loggerSplunk = LogManager.getLogger("Splunk-socket");
    protected static final Logger loggerWithContext = LogManager.getLogger("console-context");

    @Autowired
    private ESAPIValidatorService validator;

    @Autowired
    private DocumentDefencesService defenceService;

    @GetMapping("/api/document/test/")
    public boolean getStringValidationRule() {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "descriptionsome descriptionsome descriptionsome descriptionsome description");
        try {
            return validator.validateBean(boardgameDto, "boardgame_v2");
        } catch (Exception e) {
            loggerSplunk.info("ValidationRuleCOntroller - Invalid bean: " + boardgameDto.toString());
            return false;
        }

    }

    @GetMapping("/api/document/test-fail/")
    public boolean getStringValidationRuleFail() {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "<script>alert(1)</script>description avcbcdsjme description description descriptions description");
        try {
            return validator.validateBean(boardgameDto, "boardgame_v2");
        } catch (ValidationException e) {
            loggerSplunk.info("ValidationRuleController - Invalid bean: " + boardgameDto.toString());
            return false;
        } catch (Exception e) {
            loggerSplunk.info("ValidationRuleController - Invalid bean: " + boardgameDto.toString());
            return false;
        }

    }

    @PostMapping("/api/document/up")
    public void defencesUp(@RequestParam(name = "apiKey") String apiKey) {
        logger.info("Called /api/document/up/");
        defenceService.enableOrDisableDocument(true, "boardgame_v2", apiKey);

    }

    @PostMapping("/api/document/down")
    public void defencesDown(@RequestParam(name = "apiKey") String apiKey) {
        logger.info("Called /api/document/down/");
        defenceService.enableOrDisableDocument(false, "boardgame_v2", apiKey);
    }

    @GetMapping("/api/logging-test/")
    public void loggingTest(HttpServletRequest request, Principal principal) {
        if (principal != null) {
            ThreadContext.put("username", principal.getName());
        }
        if (request != null && request.getCookies().length > 0) {
            Cookie jsessionid = request.getCookies()[0];
            ThreadContext.put("IpAddress", request.getRemoteAddr());
            ThreadContext.put(jsessionid.getName(), jsessionid.getValue());
        }
        loggerWithContext.info("Logged request");
        ThreadContext.clearAll();
    }

}
