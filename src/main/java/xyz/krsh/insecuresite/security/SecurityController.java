package xyz.krsh.insecuresite.security;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.exceptions.ApiError;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class SecurityController {

    // In currentUserName(), principal may be null, therefore return a custom
    // ApiError
    @ExceptionHandler({ NullPointerException.class })
    public ApiError handleNullPointerException() {
        return new ApiError("You're not logged in", HttpStatus.NOT_FOUND);

    }

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {

        return principal.toString();

    }

}
