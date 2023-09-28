package com.tetiana.usermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetiana.usermanager.dto.UserDto;
import com.tetiana.usermanager.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private static final Long DEFAULT_ID = 1L;

    @Test
    public void testGetUserById() throws Exception {
        UserDto user = createUserDto(DEFAULT_ID);

        Mockito.when(userService.get(DEFAULT_ID)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DEFAULT_ID))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(user.getBirthDate().toString()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                .andExpect(jsonPath("$.address").value(user.getAddress()));
    }

    @Test
    public void testGetUsersByBirthDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(1990, 1, 1);
        LocalDate endDate = LocalDate.of(2000, 12, 31);

        List<UserDto> userDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userDtos.add(createUserDto((long) i));
        }

        Mockito.when(userService.getUsersByBirthDateRange(startDate, endDate)).thenReturn(userDtos);

        mockMvc.perform(get("/users")
                        .param("start_date", startDate.toString())
                        .param("end_date", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", Matchers.is(userDtos.size())));
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto userDto = createUserDto(DEFAULT_ID);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", DEFAULT_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateWholeUser() throws Exception {
        UserDto userDto = createUserDto(DEFAULT_ID);

        mockMvc.perform(put("/users/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePartUser() throws Exception {
        Map<Object, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("firstName", "NewFirstName");

        mockMvc.perform(patch("/users/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(fieldsMap)))
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
