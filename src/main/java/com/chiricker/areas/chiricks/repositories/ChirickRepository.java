package com.chiricker.areas.chiricks.repositories;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.users.models.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChirickRepository extends PagingAndSortingRepository<Chirick, String> {

    @Query("SELECT c FROM Chirick AS c WHERE c IN ?1 ORDER BY c.date DESC")
    List<Chirick> getChiricksInCollection(Set<Chirick> chiricks, Pageable pageable);

    List<Chirick> findAllByUserHandleAndParentIsNullOrderByDateDesc(String handle, Pageable pageable);

    List<Chirick> findAllByUserHandleAndParentIsNotNullOrderByDateDesc(String handle, Pageable pageable);

    List<Chirick> findAllByParentIdOrderByDateDesc(String postId, Pageable pageable);
}