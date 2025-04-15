package com.george.mdtrack.service;

import com.george.mdtrack.model.SharedLink;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.SharedLinkRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SharedLinkServiceTest {

    @Mock
    private SharedLinkRepo sharedLinkRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private SharedLinkService sharedLinkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    /**
     * Test retrieving shared links successfully
     */
    @Test
    void testGetSharedLinks_Success() {
        Long userId = 1L;
        List<SharedLink> mockLinks = List.of(new SharedLink(), new SharedLink());

        when(sharedLinkRepo.getByUserIdOrderByTimeStampDesc(userId)).thenReturn(mockLinks);

        List<SharedLink> result = sharedLinkService.getSharedLinks(userId);
        assertEquals(2, result.size());
        verify(sharedLinkRepo, times(1)).getByUserIdOrderByTimeStampDesc(userId);
    }

    /**
     * Test exception handling when retrieving shared links fails
     */
    @Test
    void testGetSharedLinks_ExceptionThrown() {
        when(sharedLinkRepo.getByUserIdOrderByTimeStampDesc(anyLong())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> sharedLinkService.getSharedLinks(1L));
    }

    /**
     * Test creating a new shared link successfully
     */
    @Test
    void testCreateSharedLink_Success() {
        Long userId = 5L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserByUserId(userId)).thenReturn(user);

        SharedLinkService spyService = Mockito.spy(sharedLinkService);

        SharedLinkRepo result = spyService.createSharedLink(userId);

        assertNotNull(result);
        verify(sharedLinkRepo, times(1)).save(any(SharedLink.class));
    }

    /**
     * Test exception handling when creating a shared link fails
     */
    @Test
    void testCreateSharedLink_Failure() {
        when(userService.getUserByUserId(anyLong())).thenThrow(new RuntimeException("User not found"));

        SharedLinkRepo result = sharedLinkService.createSharedLink(1L);
        assertNull(result);
        verify(sharedLinkRepo, never()).save(any());
    }
}
