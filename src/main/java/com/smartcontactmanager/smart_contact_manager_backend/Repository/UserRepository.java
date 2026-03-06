package com.smartcontactmanager.smart_contact_manager_backend.Repository;


import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}
