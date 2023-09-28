package com.tetiana.usermanager.service.impl;

import com.tetiana.usermanager.dto.UserDto;
import com.tetiana.usermanager.entity.User;
import com.tetiana.usermanager.exception.IncorrectDateRangeException;
import com.tetiana.usermanager.exception.NotFoundException;
import com.tetiana.usermanager.exception.UserUnderAgeException;
import com.tetiana.usermanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private static final Long DEFAULT_ID = 1L;

    @Test
    public void testGetUserById() {
        User user = createUser(DEFAULT_ID);
        UserDto userDto = createUserDto(DEFAULT_ID);

        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.get(DEFAULT_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_ID, result.getId());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getAddress(), result.getAddress());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(user.getBirthDate(), result.getBirthDate());
    }

    @Test
    public void testGetUserByIdThrowsException() {
        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.get(DEFAULT_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testCreateUser() {
        UserDto userDto = createUserDto(DEFAULT_ID);
        User user = createUser(DEFAULT_ID);

        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        userService.create(userDto);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUserThrowsException() {
        UserDto userDto = createUserDto(DEFAULT_ID);
        userDto.setBirthDate(LocalDate.of(2020, 1, 1));
        userService.setAllowedAge(18);

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(UserUnderAgeException.class);
    }

    @Test
    public void testDeleteUser() {
        userService.delete(DEFAULT_ID);
        verify(userRepository, times(1)).deleteById(DEFAULT_ID);
    }

    @Test
    public void testUpdateUser() {
        UserDto userDto = createUserDto(DEFAULT_ID);
        User user = createUser(DEFAULT_ID);

        when(modelMapper.map(userDto, User.class)).thenReturn(user);

        userService.update(DEFAULT_ID, userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testPartialUpdateUser() {
        Map<Object, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("lastName", "Updated Name");
        fieldsMap.put("birthDate", "2001-01-01");

        User user = createUser(DEFAULT_ID);
        User updatedUser = createUser(DEFAULT_ID);
        updatedUser.setLastName("Updated Name");
        updatedUser.setBirthDate(LocalDate.of(2001, 1, 1));

        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(user));

        userService.partialUpdate(DEFAULT_ID, fieldsMap);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(updatedUser, user);
    }

    @Test
    public void testGetUsersByBirthDateRange() {
        LocalDate startDate = LocalDate.of(1990, 1, 1);
        LocalDate endDate = LocalDate.of(2000, 12, 31);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(createUser((long) i));
            UserDto userDto = createUserDto((long) i);
            when(modelMapper.map(users.get(i), UserDto.class)).thenReturn(userDto);
        }

        when(userRepository.findByBirthDateBetween(startDate, endDate)).thenReturn(users);

        List<UserDto> result = userService.getUsersByBirthDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(users.size(), result.size());

        assertThatThrownBy(() -> userService.getUsersByBirthDateRange(endDate, startDate))
                .isInstanceOf(IncorrectDateRangeException.class);
    }

    private UserDto createUserDto(Long id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setEmail("user@gamil.com");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setFirstName("firstName" + id);
        user.setLastName("lastName" + id);
        user.setPhoneNumber("1234567890");
        user.setAddress("Ukraine");
        return user;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user@gamil.com");
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setFirstName("firstName" + id);
        user.setLastName("lastName" + id);
        user.setPhoneNumber("1234567890");
        user.setAddress("Ukraine");
        return user;
    }
}
