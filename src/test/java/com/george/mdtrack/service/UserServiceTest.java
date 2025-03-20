package com.george.mdtrack.service;

import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.enums.UserRoles;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class UserServiceTest {

    //Getting the UserSerice avabile for the test class
    @Autowired
    private UserService userService;

    //Using a mock repo so no data is inserted into the real database
    @MockitoBean
    private UserRepo userRepo;

    @Test
    void saveUser_SuccessfulSave_ReturnsUser() {

        //saveUser method acces just a user DTO sa we re creating one
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("testuser");
        newUserDTO.setEmail("testuser@example.com");
        newUserDTO.setPassword("password");

        //We will compare this user with the one returned bu the saveUser()
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername(newUserDTO.getUsername());
        expectedUser.setEmail(newUserDTO.getEmail());
        expectedUser.setPassword(new BCryptPasswordEncoder().encode(newUserDTO.getPassword()));
        expectedUser.setUserRole(UserRoles.USER_ROLE.toString());

        when(userRepo.save(any(User.class))).thenReturn(expectedUser);

        User savedUser = userService.saveUser(newUserDTO);

        assertNotNull(savedUser);
        assertEquals(savedUser.getId(), expectedUser.getId());
        assertEquals(savedUser.getUsername(), expectedUser.getUsername());
        assertEquals(savedUser.getEmail(), expectedUser.getEmail());
        assertEquals(savedUser.getUserRole(), expectedUser.getUserRole());
    }

    @Test
    void saveUser_NullUserRegisterDTO_ThrowsIllegalArgumentException() {
        UserRegisterDTO newUserDTO = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(newUserDTO));
        assertEquals("UserRegisterDTO is null or has null values", exception.getMessage());
    }

    @Test
    void saveUser_NullEmail_ThrowsIllegalArgumentException() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("testuser");
        newUserDTO.setEmail(null);
        newUserDTO.setPassword("password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(newUserDTO));
        assertEquals("UserRegisterDTO is null or has null values", exception.getMessage());
    }

    @Test
    void saveUser_NullUsername_ThrowsIllegalArgumentException() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername(null);
        newUserDTO.setEmail("testuser@example.com");
        newUserDTO.setPassword("password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.saveUser(newUserDTO));
        assertEquals("UserRegisterDTO is null or has null values", exception.getMessage());
    }

    @Test
    void saveUser_DatabaseException_ThrowsRuntimeException() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("testuser");
        newUserDTO.setEmail("testuser@example.com");
        newUserDTO.setPassword("password");

        doThrow(DataAccessException.class).when(userRepo).save(any(User.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUser(newUserDTO));
        assertEquals("Database exception", exception.getMessage());
    }

    @Test
    void saveUser_UnexpectedException_ThrowsRuntimeException() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("testuser");
        newUserDTO.setEmail("testuser@example.com");
        newUserDTO.setPassword("password");

        doThrow(RuntimeException.class).when(userRepo).save(any(User.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUser(newUserDTO));
        assertTrue(exception.getMessage().contains("Unknown exception"));
    }
}