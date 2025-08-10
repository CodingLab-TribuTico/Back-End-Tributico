package com.project.demo.auth;

import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.auth.AuthRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthRestControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthRestController authRestController;

    private User testUser;
    private Role testRole;

    @BeforeEach
    public void setUp() {

        testRole = new Role();
        testRole.setName(RoleEnum.USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("diego@gmail.com");
        testUser.setPassword("123");
        testUser.setRole(testRole);

        ReflectionTestUtils.setField(authRestController, "userRepository", userRepository);
        ReflectionTestUtils.setField(authRestController, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authRestController, "roleRepository", roleRepository);

    }

    @Test
    @DisplayName("Debe retornar un token válido cuando el usuario se autentica correctamente")
    public void authenticate_WithValidUser_ShouldReturnValidToken() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(authenticationService.authenticate(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("test-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<LoginResponse> response = authRestController.authenticate(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-token", response.getBody().getToken());
    }

    @Test
    @DisplayName("Debe retornar un estado de conflicto cuando el correo electrónico ya está registrado")
    void registerUser_WithExistingEmail_ShouldReturnConflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authRestController.registerUser(testUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

}
