package online.book.store.security;

import lombok.RequiredArgsConstructor;
import online.book.store.model.User;
import online.book.store.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("Can't find user be email "));
        return user;
    }
}
