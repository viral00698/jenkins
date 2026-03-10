package com.myapp.controllerTest;

import com.myapp.AuthController.AuthController;
import com.myapp.AuthController.RegisterRequest;
import com.myapp.AuthModel.User;
import com.myapp.AuthRepo.RefreshTokenRepository;
import com.myapp.AuthRepo.RoleRepo;
import com.myapp.AuthRepo.UserRepository;
import com.myapp.Token.JwtUtil;
import com.myapp.Token.RefreshTokenService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

 
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private RefreshTokenService refreshTokenService;

	@InjectMocks
	private AuthController authController;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private RoleRepo roleRepo;

	// ===============================
	// REGISTER
	// ===============================

	@Test
	void register_success() {

		RegisterRequest request = new RegisterRequest();
		request.setUsername("viral");
		request.setPassword("123");
		request.setRoles(List.of("ROLE_USER"));

		when(userRepository.existsByUsername("viral")).thenReturn(false);
		when(passwordEncoder.encode("123")).thenReturn("encoded");
		when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(jwtUtil.generateAccessToken(any(), any())).thenReturn("dummy-token");

		ResponseEntity<?> response = authController.register(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Map<?, ?> body = (Map<?, ?>) response.getBody();
		assertEquals("User registered successfully", body.get("message"));
		assertEquals("dummy-token", body.get("accessToken"));

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void register_usernameAlreadyExists() {

		RegisterRequest request = new RegisterRequest();
		request.setUsername("test");

		when(userRepository.existsByUsername("test")).thenReturn(true);

		ResponseEntity<?> response = authController.register(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Username already exists", response.getBody());

		verify(userRepository, never()).save(any());
	}

	// ====================================
	// 5️⃣ MULTIPLE ROLES
	// ====================================
	@Test
	void register_multipleRoles() {

		RegisterRequest request = new RegisterRequest();
		request.setUsername("admin");
		request.setPassword("123");
		request.setRoles(List.of("ROLE_ADMIN", "ROLE_USER"));

		when(userRepository.existsByUsername("admin")).thenReturn(false);
		when(passwordEncoder.encode("123")).thenReturn("encoded");
		when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		when(jwtUtil.generateAccessToken(any(), any())).thenReturn("dummy-token");

		ResponseEntity<?> response = authController.register(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		verify(userRepository).save(argThat(user -> user.getRoles().size() == 2));
	}

	@Test
	void register_rolesNull() {

		RegisterRequest request = new RegisterRequest();
		request.setUsername("viral");
		request.setPassword("123");
		request.setRoles(null);

		when(userRepository.existsByUsername("viral")).thenReturn(false);

		ResponseEntity<?> response = authController.register(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Role is empty", response.getBody());

		verify(userRepository, never()).save(any());
	}

	@Test
	void register_rolesEmpty() {

		RegisterRequest request = new RegisterRequest();
		request.setUsername("viral");
		request.setPassword("123");
		request.setRoles(List.of());

		when(userRepository.existsByUsername("viral")).thenReturn(false);

		ResponseEntity<?> response = authController.register(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Role is empty", response.getBody());

		verify(userRepository, never()).save(any());
	}

	@BeforeAll
	static void init() {
		System.err.println("Init");
	}

	@BeforeEach
	void setup() {
		System.err.println("BeforEach");
	}

}
