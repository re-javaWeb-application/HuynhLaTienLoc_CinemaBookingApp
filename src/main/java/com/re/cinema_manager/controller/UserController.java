package com.re.cinema_manager.controller;


import com.re.cinema_manager.model.dto.LoginDto;
import com.re.cinema_manager.model.dto.RegisterRequestDTO;
import com.re.cinema_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping({"/register"})
    public String showRegisterPage(){
        return "register";
    }

    @PostMapping({"/register"})
    public String register(RegisterRequestDTO registerRequestDTO){
        userService.Register(registerRequestDTO);
        return "redirect:/login";
    }

    @GetMapping({"/login"})
    public String showLoginPage(){
        return "login";
    }

    @PostMapping({"/login"})
    public String login(LoginDto loginDto){
        userService.Login(loginDto);
        return "home";
    }

    @GetMapping({"/home"})
    public String showHomePage(){
        return "home";
    }
}
