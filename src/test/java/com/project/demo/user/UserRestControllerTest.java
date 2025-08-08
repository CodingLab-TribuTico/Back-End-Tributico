package com.project.demo.user;

import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.user.UserRestController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRestControllerTest {

    @Mock
    private UserRepository userRepository;

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

        StringBuffer reqURL = new StringBuffer("http://localhost");
        when(httpServletRequest.getRequestURL()).thenReturn(reqURL);
    }

    @Test
    @DisplayName("Debe crear un usuario")
    void addUser_WithValidData_ShouldReturnOk() {
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(userRepository.save(eq(testUser))).thenReturn(testUser);

        ResponseEntity<?> response = userRestController.addUser(testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Debe retornar una lista paginada con todos los usuarios")
    void getAllUsers_WhenUsersExist_ShouldReturnPaginatedList() {
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(testUser));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usersPage);

        ResponseEntity<?> response = userRestController.getAll(1, 5, "", httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Debe actualizar un usuario cuando el ID proporcionado es válido")
    void updateUser_WithExistingId_ShouldReturnOk() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(testUser));
        when(userRepository.save(eq(testUser))).thenReturn(testUser);

        ResponseEntity<?> response = userRestController.updateUser(1L, testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar un usuario cuando el ID proporcionado es válido")
    void deleteUser_WithExistingId_ShouldReturnOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(anyLong());

        ResponseEntity<?> response = userRestController.deleteUser(1L, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).deleteById(1L);
    }

}
