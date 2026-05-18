package com.re.cinema_manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    private String username;
    private String password;

    private String fullName;
    private String email;
    private String phone;
}
