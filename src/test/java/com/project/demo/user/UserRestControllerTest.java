package com.project.demo.user;

import com.project.demo.logic.entity.http.HttpResponse;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.user.UserRestController;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRestControllerTest {

    private static final String BASE_URL = "http://localhost:8080/users";


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

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(BASE_URL));
    }


    @Test
    @DisplayName("Debe crear un usuario")
    void addUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(userRepository.save(any())).thenReturn(testUser);

        ResponseEntity<?> response = userRestController.addUser(testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Object responseBody = response.getBody();
        assert responseBody instanceof HttpResponse;

        HttpResponse httpResponse = (HttpResponse) responseBody;
        assertEquals("Usuario creado con éxito", httpResponse.getMessage());
        assertEquals(testUser, httpResponse.getData());
    }







}
