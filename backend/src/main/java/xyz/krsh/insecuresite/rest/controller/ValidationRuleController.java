package xyz.krsh.insecuresite.rest.controller;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.service.DocumentDefencesService;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;
import xyz.krsh.insecuresite.security.LoggerWrapper;

@RestController
public class ValidationRuleController {

    protected static final Logger logger = LogManager.getLogger();
    protected LoggerWrapper loggerWrapper = new LoggerWrapper();

    @Autowired
    private ESAPIValidatorService validator;

    @Autowired
    private DocumentDefencesService defenceService;

    @GetMapping("/api/document/test/")
    public boolean getStringValidationRule(HttpServletRequest request) {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "descriptionsome descriptionsome descriptionsome descriptionsome description");
        try {
            loggerWrapper.log("ESAPIValidatorService - Validating: " + boardgameDto, request);
            return validator.validateBean(boardgameDto, "boardgame_v2");
        } catch (Exception e) {
            logger.warn(e);
            loggerWrapper.log(
                    "Caught Exception + " + e + "- Invalid bean: " + boardgameDto.toString(), request,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }

    }

    @GetMapping("/api/document/test-fail/")
    public boolean getStringValidationRuleFail(HttpServletRequest request) {
        BoardgameDto boardgameDto = new BoardgameDto("someValue2", (float) 2.2, 4,
                "<script>alert(1)</script>description avcbcdsjme description description descriptions description");
        try {
            loggerWrapper.log("ESAPIValidatorService - Validating: " + boardgameDto, request);
            return validator.validateBean(boardgameDto, "boardgame_v2");
        } catch (Exception e) {
            logger.warn(e);
            loggerWrapper.log(
                    "ValidationRuleController - Invalid bean: " + boardgameDto.toString(), request,
                    HttpStatus.BAD_REQUEST.value());
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
    public void loggingTest(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            ThreadContext.put("username", principal.getName());
        }
        if (request != null && request.getCookies().length > 0) {
            Cookie jsessionid = request.getCookies()[0];
            ThreadContext.put("IpAddress", request.getRemoteAddr());
            ThreadContext.put(jsessionid.getName(), jsessionid.getValue());
        }
        // loggerWithContext.info("Logged request");
        ThreadContext.clearAll();
    }

}
