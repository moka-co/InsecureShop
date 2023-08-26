package xyz.krsh.insecuresite.rest.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import xyz.krsh.insecuresite.exceptions.ApiError;

import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@Tag(name = "Security Controller", description = "This controller is responsible for handling specific HTTP requests related to user authentication and authorization.")
public class SecurityController {

    /*
     * Returns currently logged username (email)
     */
    @ApiResponse(description = "Returns currently logged username")
    @GetMapping(value = "/api/username")
    @ResponseBody
    public String currentUserName(Principal principal) {
        return principal.getName();

    }

    /*
     * Returns true if the user is logged in
     */
    @ApiResponse(description = "Returns true if user is logged in")
    @GetMapping(value = "/api/check_login")
    @ResponseBody
    public boolean checkAmILoggedIn(Principal principal) {
        return principal == null ? false : true;
    }

    /*
     * Returns true if logged user is admin
     */
    @ApiResponse(description = "Returns true if logged user is admin")
    @GetMapping(value = "/api/is_admin")
    @ResponseBody
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        boolean isAdmin = authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> "admin".equals(authority.getAuthority()));

        return isAdmin;
    }

    @ExceptionHandler({ NullPointerException.class })
    public ApiError handleNullPointerException() {
        return new ApiError("You're not logged in", HttpStatus.NOT_FOUND);

    }

}
