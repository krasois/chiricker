package com.chiricker.chiricks.repositories;

import com.chiricker.chiricks.models.entities.Chirick;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChirickRepository extends PagingAndSortingRepository<Chirick, String> {

    @Query("SELECT c FROM Chirick AS c INNER JOIN c.user AS u WHERE u.handle = ?1 AND c.parent IS NULL ORDER BY c.date DESC")
    List<Chirick> getChiricksByUser(String handle, Pageable pageable);

    @Query("SELECT c FROM Chirick AS c INNER JOIN c.user AS u WHERE u.handle = ?1 AND c.parent IS NOT NULL ORDER BY c.date DESC")
    List<Chirick> getCommentsByUser(String handle, Pageable pageable);

    @Query("SELECT c FROM Chirick AS c WHERE c IN ?1 ORDER BY c.date DESC")
    List<Chirick> getChiricksInCollection(Set<Chirick> chiricks, Pageable pageable);

    @Query("SELECT c FROM Chirick AS c JOIN c.parent AS p WHERE p.id = ?1 ORDER BY c.date DESC")
    List<Chirick> getAllCommentsFromPost(String postId, Pageable pageable);
}