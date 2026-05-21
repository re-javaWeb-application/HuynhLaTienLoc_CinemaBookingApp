package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(Role role);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userProfile
            ORDER BY u.createdAt DESC
            """)
    List<User> findAllWithProfile();

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userProfile
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithProfile(@Param("id") Long id);
}
