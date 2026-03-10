package com.myapp.Token;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myapp.AuthModel.User;
import com.myapp.AuthRepo.RefreshTokenRepository;
import com.myapp.AuthRepo.RoleRepo;
import com.myapp.AuthRepo.UserRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;
    
    private final RoleRepo roleRepo;

    private final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; // 7 days

    public RefreshTokenService(RefreshTokenRepository repo,
                               UserRepository userRepo , RoleRepo roleRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {

         User user = userRepo.findByUsername(username)
            .orElseThrow();

        repo.deleteByUser(user); // one refresh token per user

        RefreshToken token = new RefreshToken();
        
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(REFRESH_EXP));

        return repo.save(token);
    }

    public RefreshToken verify(String token) {

        RefreshToken refreshToken = repo.findByToken(token)
            .orElseThrow(() ->
                new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
}