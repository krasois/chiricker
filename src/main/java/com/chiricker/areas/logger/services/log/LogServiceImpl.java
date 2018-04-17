package com.chiricker.areas.logger.services.log;

import com.chiricker.areas.logger.models.entities.Log;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.logger.models.service.LogServiceModel;
import com.chiricker.areas.admin.models.view.LogViewModel;
import com.chiricker.areas.logger.repositories.LogRepository;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Autowired
    public LogServiceImpl(LogRepository logRepository, UserService userService, ModelMapper mapper) {
        this.logRepository = logRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    private LogViewModel mapLog(Log log) {
        return this.mapper.map(log, LogViewModel.class);
    }

    @Async
    @Override
    @Transactional
    public LogServiceModel createLog(String handle, Operation operation, String modifiedTable) throws UserNotFoundException {
        UserServiceModel user = null;
        if (handle != null) {
            user = this.userService.getByHandle(handle);
            if (user == null) throw new UserNotFoundException();
        }

        String userId = user == null ? null : user.getId();

        Log log = new Log();
        log.setUserId(userId);
        log.setOperation(operation);
        log.setModifiedTable(modifiedTable);

        this.logRepository.save(log);

        return this.mapper.map(log, LogServiceModel.class);
    }

    @Override
    public Page<LogViewModel> getLogs(Pageable pageable) {
        Page<Log> logs = this.logRepository.findAll(pageable);
        return logs.map(this::mapLog);
    }
}