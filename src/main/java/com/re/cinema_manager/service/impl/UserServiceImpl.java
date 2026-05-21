package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.auth.LoginDto;
import com.re.cinema_manager.dto.auth.RegisterRequestDTO;
import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.model.entity.UserProfile;
import com.re.cinema_manager.repository.UserProfileRepository;
import com.re.cinema_manager.repository.UserRepository;
import com.re.cinema_manager.service.UserService;
import com.re.cinema_manager.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public void Register(RegisterRequestDTO userDto){

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());

        user.setPassword(PasswordUtil.createHash(userDto.getPassword()));

        user.setRole(Role.CUSTOMER);

        User saveUser = userRepository.save(user);

        //Luu userProfile
        UserProfile profile = new UserProfile();
        profile.setUser(saveUser); // Nối khóa ngoại (Foreign Key)
        profile.setFullName(userDto.getFullName());
        profile.setEmail(userDto.getEmail());
        profile.setPhone(userDto.getPhone());

        userProfileRepository.save(profile);
    }

    @Override
    public User Login(LoginDto loginDto){
        User user = userRepository.findByUsername(loginDto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        Boolean checkPass = PasswordUtil.verifyPassword(loginDto.getPassWord(),user.getPassword());

        if(!checkPass){
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        return user;
    }

}
