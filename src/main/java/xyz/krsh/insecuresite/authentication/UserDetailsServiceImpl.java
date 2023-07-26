package xyz.krsh.insecuresite.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email); // Get user from the database

        if (user == null) {
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
