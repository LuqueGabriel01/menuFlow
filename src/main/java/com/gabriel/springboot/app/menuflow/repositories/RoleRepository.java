package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.Role;
import com.gabriel.springboot.app.menuflow.models.entities.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
