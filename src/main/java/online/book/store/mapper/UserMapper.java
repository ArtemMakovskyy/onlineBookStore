package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.user.UserResponseDto;
import online.book.store.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);
}
