package com.myapp.serviceTest;


import com.myapp.AuthModel.User;
import com.myapp.AuthRepo.RefreshTokenRepository;
import com.myapp.AuthRepo.UserRepository;
import com.myapp.Token.RefreshToken;
import com.myapp.Token.RefreshTokenService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private RefreshTokenService service;

    // ===============================
    // createRefreshToken() Tests
    // ===============================

    @Test
    void createRefreshToken_validUser_returnsToken() {

        String username = "viral";
        User user = new User();
        user.setUsername(username);

        when(userRepo.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(repo.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = service.createRefreshToken(username);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());

        verify(repo).deleteByUser(user);
        verify(repo).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_userNotFound_throwsException() {

        when(userRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.createRefreshToken("unknown"));

        verify(repo, never()).save(any());
    }

    // ===============================
    // verify() Tests
    // ===============================

    @Test
    void verify_validToken_returnsRefreshToken() {

        RefreshToken token = new RefreshToken();
        token.setToken("70fdc51d-aa99-4ebd-af9d-f82bf90c0887");
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        when(repo.findByToken("70fdc51d-aa99-4ebd-af9d-f82bf90c0887"))
                .thenReturn(Optional.of(token));

        RefreshToken result = service.verify("70fdc51d-aa99-4ebd-af9d-f82bf90c0887");

        assertEquals("70fdc51d-aa99-4ebd-af9d-f82bf90c0887", result.getToken());
        verify(repo, never()).delete(any());
    }

    @Test
    void verify_invalidToken_throwsException() {

        when(repo.findByToken("wrong"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.verify("wrong"));

        verify(repo, never()).delete(any());
    }

 
}