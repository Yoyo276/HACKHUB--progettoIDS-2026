package com.example.repository;

import com.example.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Questo metodo ci serve per cercare "ROLE_MEMBRO_TEAM" nel DB
    Optional<Role> findByName(String name);
}