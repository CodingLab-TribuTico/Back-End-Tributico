package com.project.demo.logic.entity.goals;

import com.project.demo.logic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalsRepository extends JpaRepository<Goals, Long> {

    List<Goals> findByUser(User user);

    List<Goals> findByUserAndDeclaration(User user, String declaration);
}