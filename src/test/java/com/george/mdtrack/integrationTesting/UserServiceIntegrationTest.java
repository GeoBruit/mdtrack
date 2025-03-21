package com.george.mdtrack.integrationTesting;


import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.User;
import com.george.mdtrack.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@SpringBootTest
public class UserServiceIntegrationTest {


    @Autowired
    private UserService userService;


    @Test
    public void testSaveUser() {

        //Creating user dto to save to the database
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("Username");
        newUserDTO.setEmail("testuser@example.com");
        newUserDTO.setPassword("password");

        User savedUser = userService.saveUser(newUserDTO);

        assert savedUser != null;
        assert Objects.equals(savedUser.getUsername(), newUserDTO.getUsername());
        assert Objects.equals(savedUser.getEmail(), newUserDTO.getEmail());

    }


    @Test
    public void testSaveUserWithNullValues() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername(null);
        newUserDTO.setEmail(null);
        newUserDTO.setPassword(null);

        assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(newUserDTO));
    }

    @Test
    public void testSaveUserWithEmptyValues() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("");
        newUserDTO.setEmail("");
        newUserDTO.setPassword("");

        assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(newUserDTO));
    }


    @Test
    public void testGetUserByIdThrowsError(){

        Long id = 1000L; //setting an id that does not exists

        assertThrows(IllegalArgumentException.class, () ->
                userService.getUserByUserId(id));
    }
}
