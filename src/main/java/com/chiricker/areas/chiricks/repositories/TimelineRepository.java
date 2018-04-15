package com.chiricker.areas.chiricks.repositories;

import com.chiricker.areas.chiricks.models.entities.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, String> {
}