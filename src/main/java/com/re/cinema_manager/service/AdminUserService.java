package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.admin.AdminUserFormDto;
import com.re.cinema_manager.dto.admin.AdminUserListItemDto;

import java.util.List;

public interface AdminUserService {

    List<AdminUserListItemDto> findAllUsers();

    AdminUserFormDto getFormForEdit(Long userId);

    void createUser(AdminUserFormDto form);

    void updateUser(Long userId, AdminUserFormDto form);

    void deleteUser(Long userId, Long currentAdminId);
}
