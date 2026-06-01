package com.hr.airline.ripo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.airline.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String name);
}
