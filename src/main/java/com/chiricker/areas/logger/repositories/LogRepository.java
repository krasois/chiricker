package com.chiricker.areas.logger.repositories;

import com.chiricker.areas.logger.models.entities.Log;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends PagingAndSortingRepository<Log, String> {
}