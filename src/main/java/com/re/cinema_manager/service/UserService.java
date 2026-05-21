package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.auth.LoginDto;
import com.re.cinema_manager.dto.auth.RegisterRequestDTO;
import com.re.cinema_manager.model.entity.User;
import jakarta.transaction.Transactional;

public interface UserService {

    @Transactional
    void Register(RegisterRequestDTO userDto);

    User Login(LoginDto loginDto);

}

