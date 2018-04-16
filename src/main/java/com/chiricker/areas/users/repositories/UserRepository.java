package com.chiricker.areas.users.repositories;

import com.chiricker.areas.users.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    boolean existsByHandleIs(String handle);

    User findByHandle(String handle);

    Page<User> findAllByIsEnabledTrue(Pageable pageable);

    Page<User> findAllByIsEnabledFalse(Pageable pageable);

    User findByIsEnabledIsTrueAndHandle(String handle);

    List<User> findAllByFollowingContainingOrderByHandle(User handle, Pageable pageable);

    List<User> findAllByFollowersContainingOrderByHandle(User handle, Pageable pageable);

    Page<User> findAllByNameContainingOrderByHandle(String name, Pageable pageable);
}
