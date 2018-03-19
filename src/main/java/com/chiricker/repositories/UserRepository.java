package com.chiricker.repositories;

import com.chiricker.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByHandleIs(String handle);

    User findByHandle(String handle);

    boolean existsByHandleAndPassword(String handle, String password);
}
