package com.re.cinema_manager.interceptor;

import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Chỉ tài khoản STAFF được truy cập /staff/**.
 */
@Component
public class StaffInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || user.getRole() != Role.STAFF) {
            session.setAttribute(
                    "accessDeniedMessage",
                    "Khu vực nhân viên chỉ dành cho tài khoản Nhân viên. Vui lòng đăng nhập đúng vai trò."
            );
            response.sendRedirect("/home");
            return false;
        }
        return true;
    }
}
