package com.myapp.AuthController;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.AuthDto.LoginRequest;
import com.myapp.AuthDto.LoginResponse;
import com.myapp.AuthDto.RefreshRequest;
import com.myapp.AuthModel.Role;
import com.myapp.AuthModel.User;
import com.myapp.AuthRepo.RefreshTokenRepository;
import com.myapp.AuthRepo.RoleRepo;
import com.myapp.AuthRepo.UserRepository;
import com.myapp.Token.JwtUtil;
import com.myapp.Token.RefreshToken;
import com.myapp.Token.RefreshTokenService;
import com.myapp.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenRepository refreshTokenRepository;

	private final RoleRepo roleRepo;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
			AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService,
			RefreshTokenRepository refreshTokenRepository, RoleRepo roleRepo) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
		this.refreshTokenService = refreshTokenService;
		this.refreshTokenRepository = refreshTokenRepository;
		this.roleRepo = roleRepo;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

		if (userRepository.existsByUsername(request.getUsername())) {
			return ResponseEntity.badRequest().body("Username already exists");
		}
		
		if(request.getRoles() == null || request.getRoles().isEmpty() ) {
			return ResponseEntity.badRequest().body("Role is empty");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		List<String> roles = request.getRoles(); 

		// Add roles to user
		for (String roleName : roles) {
			Role role = new Role();
			role.setRoleName(roleName);
			user.addRole(role);
		}

		// Save user (this also saves roles because of CascadeType.ALL)
		userRepository.save(user);

		// Convert roles to GrantedAuthority for JWT
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getRoleName())).toList();

		// Generate token
		String token = jwtUtil.generateAccessToken(user.getUsername(), authorities);
		return ResponseEntity.ok(Map.of("message", "User registered successfully", "accessToken", token));
	}

	@PostMapping("login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

		// 🔐 Authenticate username & password
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		// 👤 Authenticated user
		UserDetails user = (UserDetails) authentication.getPrincipal();

		// 🎫 Generate JWT

		String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getAuthorities());

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

		return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken.getToken()));
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request) {

		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenRepository.findByToken(requestRefreshToken).map(refreshToken -> {

			if (refreshToken.getExpiryDate().isBefore(Instant.now())) {

				refreshTokenRepository.delete(refreshToken);
				throw new RuntimeException("Refresh token expired");
			}

			String username = refreshToken.getUser().getUsername();

			List<Role> roles = roleRepo.findByUserId(refreshToken.getUser().getId());

			Collection<? extends GrantedAuthority> authorities = roles.stream()
					.map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());

			String newAccessToken = jwtUtil.generateAccessToken(username, authorities);

			return ResponseEntity.ok(new LoginResponse(newAccessToken, requestRefreshToken));

		}).orElseThrow(() -> new RuntimeException("Refresh token not found"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {

		refreshTokenRepository.findByToken(request.getRefreshToken()).ifPresent(refreshTokenRepository::delete);

		return ResponseEntity.ok("Logged out successfully");
	}

	@GetMapping("/exception")
	public String testException() {
		throw new ResourceNotFoundException("User not found");
	}

}