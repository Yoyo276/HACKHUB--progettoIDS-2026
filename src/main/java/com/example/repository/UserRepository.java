package com.example.repository;

import com.example.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Import fondamentale

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Cambiamo il ritorno da User a Optional<User>
    Optional<User> findByUsername(String username);
}