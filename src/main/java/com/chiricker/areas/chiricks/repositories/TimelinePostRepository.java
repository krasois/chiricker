package com.chiricker.areas.chiricks.repositories;

import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TimelinePostRepository extends PagingAndSortingRepository<TimelinePost, String> {

    @Query("SELECT p FROM TimelinePost AS p WHERE p IN ?1 ORDER BY p.date DESC")
    Page<TimelinePost> findAllInSet(Set<TimelinePost> posts, Pageable pageable);
}