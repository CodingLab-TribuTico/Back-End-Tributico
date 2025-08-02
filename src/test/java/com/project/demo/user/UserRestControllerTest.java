package com.project.demo.user;

import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.user.UserRestController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRestControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRestControllerTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRestController userRestController;

    private User testUser;
    private Role testRole;


    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setName(RoleEnum.USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Diego");
        testUser.setLastname("Nunez");
        testUser.setLastname2("Brenes");
        testUser.setEmail("diego@gmail.com");
        testUser.setIdentification("123456789");
        testUser.setBirthDate(LocalDate.now());
        testUser.setPassword("123");
        testUser.setRole(testRole);

    }

    @Test
    @DisplayName("Debe crear un usuario")
    void addUserTest() {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any())).thenReturn(testUser);

        ResponseEntity<?> response = userRestController.addUser(testUser, httpServletRequest);

        logger.info(() -> "Datos de salida: \n" +
                "Código de estado: " + response.getStatusCode());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));

    }

    @Test
    @DisplayName("Debe devolver una lista paginada con los usuarios")
    void getAllUsersTest() {
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(testUser));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usersPage);
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(httpServletRequest.getMethod()).thenReturn("GET");

        ResponseEntity<?> response = userRestController.getAll(1, 5, "", httpServletRequest);

        logger.info(() -> "Datos de salida:\nStatus: " + response.getStatusCode());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).findAll(any(Pageable.class));
    }

}
