package xyz.krsh.insecuresite.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import xyz.krsh.insecuresite.rest.entities.User;
import xyz.krsh.insecuresite.rest.repository.UserRepository;
import xyz.krsh.insecuresite.security.LoggerWrapper;
import xyz.krsh.insecuresite.security.MyUserDetails;

import org.apache.logging.log4j.Logger;
import org.owasp.esapi.errors.ValidationException;
import org.apache.logging.log4j.LogManager;

public class UserDetailsServiceImpl implements UserDetailsService {

    protected static final Logger logger = LogManager.getLogger();
    protected static final LoggerWrapper loggerSplunk = new LoggerWrapper();

    @Autowired
    ESAPIValidatorService validator;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) // Load users from database with JPA Repository pattern
            throws UsernameNotFoundException {
        logger.info("New login attempt detected: " + email);
        User user = userRepository.getUserByEmail(email); // Get user from the database

        if (user == null) {
            loggerSplunk.log(null, "User not found");
            throw new UsernameNotFoundException("Could not find user");
        }

        // Java Spring uses BCrypt and checks the password against the hash
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // Encode the password before passing it to the security context otherwise it
        // will not be recognized
        user.setPassword(encoder.encode(user.getPassword()));

        return new MyUserDetails(user);
    }

}
