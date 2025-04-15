package com.george.mdtrack.service;


import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.model.UserProfile;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserProfileRepo;
import com.george.mdtrack.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {

    /*
     * Mocked dependencies for UserService
     */
    @Mock
    private UserRepo userRepo;

    @Mock
    private MedicalNoteRepo medicalNoteRepo;

    @Mock
    private UserProfileRepo userProfileRepo;

    /*
     * The service under test, injected with mocks above
     */
    @InjectMocks
    private UserService userService;

    /*
     * Initialize mocks before each test
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
     * Test retrieving user by ID when user exists
     */
    @Test
    void testGetUserByUserId_UserExists_ReturnsUser() {
        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserByUserId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /*
     * Test retrieving user by ID when user does not exist
     */
    @Test
    void testGetUserByUserId_UserNotFound_ThrowsException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getUserByUserId(1L));
    }

    /*
     * Test getting user profile by user ID when profile exists
     */
    @Test
    void testGetUserProfileByUserId_ProfileExists() {
        UserProfile profile = new UserProfile();
        when(userProfileRepo.findByUserId(1L)).thenReturn(profile);

        UserProfile result = userService.getUserProfileByUserId(1L);
        assertEquals(profile, result);
    }

    /*
     * Test getting user profile by user ID when profile is not found
     */
    @Test
    void testGetUserProfileByUserId_ProfileNotFound() {
        when(userProfileRepo.findByUserId(1L)).thenReturn(null);

        UserProfile result = userService.getUserProfileByUserId(1L);
        assertNotNull(result); // Returns a new UserProfile if null
    }

    /*
     * Test saving a user profile for an existing user
     */
    @Test
    void testSaveUserProfile_ValidUser_SavesProfile() {
        User user = new User();
        user.setId(1L);
        UserProfile profile = new UserProfile();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.saveUserProfile(profile, 1L));
        verify(userRepo, times(1)).save(user);
    }

    /*
     * Test saving user profile for a non-existent user
     */
    @Test
    void testSaveUserProfile_UserNotFound_ThrowsException() {
        UserProfile profile = new UserProfile();
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.saveUserProfile(profile, 1L));
    }

    /*
     * Test saving a new user with valid registration data
     */
    @Test
    void testSaveUser_ValidDTO_Success() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.saveUser(dto);
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertTrue(savedUser.getEmail().contains("@"));
    }

    /*
     * Test searching patients by full name
     */
    @Test
    void testSearchPatients_FullName() {
        String query = "John Smith";
        List<User> expected = List.of(new User());

        when(userRepo.searchByFirstAndLastName("John", "Smith")).thenReturn(expected);

        List<User> result = userService.searchPatients(query);
        assertEquals(expected, result);
    }

    /*
     * Test searching patients by single name
     */
    @Test
    void testSearchPatients_SingleName() {
        String query = "John";
        List<User> expected = List.of(new User());

        when(userRepo.searchByFirstOrLastName("John")).thenReturn(expected);

        List<User> result = userService.searchPatients(query);
        assertEquals(expected, result);
    }
}