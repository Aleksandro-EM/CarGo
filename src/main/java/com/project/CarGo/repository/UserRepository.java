package com.project.CarGo.repository;

import com.project.CarGo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
    @Modifying
    @Query("update User u set u.role = :role where u.id = :id")
    int updateRole(@Param("id") long id, @Param("role") String role);
}
