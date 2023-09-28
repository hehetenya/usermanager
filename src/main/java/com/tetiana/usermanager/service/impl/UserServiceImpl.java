package com.tetiana.usermanager.service.impl;

import com.tetiana.usermanager.controller.UserController;
import com.tetiana.usermanager.dto.UserDto;
import com.tetiana.usermanager.entity.User;
import com.tetiana.usermanager.exception.IncorrectDateRangeException;
import com.tetiana.usermanager.exception.NotFoundException;
import com.tetiana.usermanager.exception.UserUnderAgeException;
import com.tetiana.usermanager.repository.UserRepository;
import com.tetiana.usermanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
@Setter
@PropertySource("classpath:custom.properties")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Value("${allowed_age}")
    private int allowedAge;

    @Override
    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user with id " + id));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public void create(UserDto userDto) {
        verifyAge(userDto.getBirthDate());
        userRepository.save(modelMapper.map(userDto, User.class));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(Long id, UserDto userDto) {
        verifyAge(userDto.getBirthDate());
        User user = modelMapper.map(userDto, User.class);
        user.setId(id);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void partialUpdate(Long id, Map<Object, Object> fieldsMap) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user with id " + id));
        fieldsMap.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, (String) key);
            if (field != null) {
                if (field.getName().equals("birthDate")) {
                    LocalDate birthDate = LocalDate.parse((String) fieldsMap.get("birthDate"));
                    verifyAge(birthDate);
                    value = birthDate;
                }
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
            }
        });
        userRepository.save(user);
    }

    @Override
    public List<UserDto> getUsersByBirthDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IncorrectDateRangeException("Start date should be before end date");
        }
        List<User> users = userRepository.findByBirthDateBetween(start, end);
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = modelMapper.map(user, UserDto.class);
            Link view = linkTo(methodOn(UserController.class)
                    .get(user.getId()))
                    .withRel("view");
            userDto.add(view);
            userDtos.add(userDto);
        }
        return userDtos;
    }

    private void verifyAge(LocalDate birthDate) {
        Period difference = Period.between(birthDate, LocalDate.now());
        if (difference.getYears() < allowedAge) {
            throw new UserUnderAgeException("Users under " + allowedAge + " y.o. are not allowed");
        }
    }
}
