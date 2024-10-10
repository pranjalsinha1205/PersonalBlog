package com.personalblogging.PersonalBlog.role.repository;

import com.personalblogging.PersonalBlog.role.model.Role;
import com.personalblogging.PersonalBlog.role.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(RoleType role);
}
