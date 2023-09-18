package online.book.store.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.user.UserRegistrationRequestDto;
import online.book.store.dto.user.UserResponseDto;
import online.book.store.exception.RegistrationException;
import online.book.store.mapper.UserMapper;
import online.book.store.model.Role;
import online.book.store.model.ShoppingCart;
import online.book.store.model.User;
import online.book.store.repository.role.RoleRepository;
import online.book.store.repository.shopping.cart.ShoppingCartRepository;
import online.book.store.repository.user.UserRepository;
import online.book.store.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setShippingAddress(request.getShippingAddress());
        Role roleUser =
                roleRepository.findById(2L).orElseThrow(
                        () -> new RuntimeException("Can't find ROLE_USER by id"));
        user.setRoles(Set.of(roleUser));
        // TODO: 14.09.2023 Create ShoppingCart
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
        final User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
