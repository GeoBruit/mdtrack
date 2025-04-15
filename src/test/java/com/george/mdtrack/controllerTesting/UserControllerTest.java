package com.george.mdtrack.controller;

import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.User;
import com.george.mdtrack.service.MedicalDocumentService;
import com.george.mdtrack.service.MedicalNoteService;
import com.george.mdtrack.service.SharedLinkService;
import com.george.mdtrack.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser // Simulates an authenticated user to bypass Spring Security
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private UserService userService;
    @MockitoBean private MedicalNoteService medicalNoteService;
    @MockitoBean private MedicalDocumentService medicalDocumentService;
    @MockitoBean private SharedLinkService sharedLinkService;

    private UserRegisterDTO mockUserDto;

    @BeforeEach
    void setUp() {
        mockUserDto = new UserRegisterDTO();
        mockUserDto.setUsername("john");
        mockUserDto.setEmail("john@example.com");
        mockUserDto.setPassword("secure123");
    }

    /**
     * Test GET /register-form — should return the registration view and model attribute.
     */
    @Test
    void testGetRegisterForm_ReturnsView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("form/register-form"))
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Test POST /register — should save user and redirect to login page.
     */
    @Test
    void testPostRegisterForm_RedirectsToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("userRegisterDTO", mockUserDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-form"));

        Mockito.verify(userService).saveUser(any(UserRegisterDTO.class));
    }

    /**
     * Test GET /login-form — should return login page view.
     */
    @Test
    void testGetLoginForm_ReturnsLoginPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("form/login-form"));
    }
}
