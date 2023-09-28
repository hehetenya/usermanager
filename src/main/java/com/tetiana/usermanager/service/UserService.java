package com.tetiana.usermanager.service;

import com.tetiana.usermanager.dto.UserDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface UserService {
    UserDto get(Long id);

    List<UserDto> getUsersByBirthDateRange(LocalDate start, LocalDate end);

    void create(UserDto user);

    void delete(Long id);

    void update(Long id, UserDto user);

    void partialUpdate(Long id, Map<Object, Object> fieldsMap);
}
