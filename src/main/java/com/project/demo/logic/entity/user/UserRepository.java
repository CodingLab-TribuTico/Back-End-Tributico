package com.project.demo.logic.entity.user;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>  {
    @Query("""
    SELECT u FROM User u 
    WHERE 
        LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
        LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR 
        LOWER(u.lastname2) LIKE LOWER(CONCAT('%', :search, '%')) OR 
        LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query("""
    SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)
    """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u")
    Page<User> findAll(Pageable pageable);
}
