package xyz.krsh.insecuresite.rest.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.exceptions.ApiError;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class SecurityController {

    @ExceptionHandler({ NullPointerException.class })
    public ApiError handleNullPointerException() {
        return new ApiError("You're not logged in", HttpStatus.NOT_FOUND);

    }

    @RequestMapping(value = "/api/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {

        return principal.toString();

    }

    @RequestMapping(value = "/api/check_login", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkAmILoggedIn(Principal principal) {
        return principal == null ? false : true;
        /*
         * if (principal == null) {
         * return false;
         * }
         * return true;
         */
    }

    @RequestMapping(value = "/api/is_admin", method = RequestMethod.GET)
    @ResponseBody
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> "admin".equals(authority.getAuthority()));

        return isAdmin;
    }

}
