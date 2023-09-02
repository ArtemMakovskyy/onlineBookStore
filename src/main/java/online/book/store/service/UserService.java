package online.book.store.service;

import online.book.store.dto.user.UserRegistrationRequestDto;
import online.book.store.dto.user.UserResponseDto;
import online.book.store.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
