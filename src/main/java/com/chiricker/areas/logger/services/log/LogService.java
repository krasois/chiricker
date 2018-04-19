package com.chiricker.areas.logger.services.log;

import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.logger.models.service.LogServiceModel;
import com.chiricker.areas.admin.models.view.LogViewModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogService {

    LogServiceModel createLog(String handle, Operation operation, String modifiedTable);

    Page<LogViewModel> getLogs(Pageable pageable);
}