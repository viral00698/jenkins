package com.myapp.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.myapp.AuthModel.User;
import com.myapp.AuthRepo.UserRepository;
import com.myapp.AuthService.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void loadUserByUsername_validUsername_returnsUserDetails() {

		// Given
		String username = "viral";

		User user = new User();
		user.setUsername(username);
		user.setPassword("password");

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

		// When
		UserDetails result = customUserDetailsService.loadUserByUsername(username);

		// Then
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		verify(userRepository, times(1)).findByUsername(username);
	}

	@Test
	void loadUserByUsername_invalidUsername_throwsException() {
		 
		// Given
		String username = "unknown";

		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

		// When + Then
		assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));

		verify(userRepository, times(1)).findByUsername(username);
	}

}
