package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.user.UserLoginRequestDto;
import online.book.store.dto.user.UserLoginResponseDto;
import online.book.store.dto.user.UserRegistrationRequestDto;
import online.book.store.dto.user.UserResponseDto;
import online.book.store.exception.RegistrationException;
import online.book.store.security.AuthenticationService;
import online.book.store.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Management authentication", description = "Endpoints to login and register")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Registered user login.",
            description = "Input email address and password to login.")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @Operation(summary = "Registration of a new user.",
            description = "Save your: email, password,first name, "
                    + "last name and shipping address for registration.")
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
