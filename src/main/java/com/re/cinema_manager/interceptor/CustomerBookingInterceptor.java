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
 * Đặt vé / lịch sử vé trên web chỉ dành cho CUSTOMER (không gồm STAFF/ADMIN).
 */
@Component
public class CustomerBookingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        if (user != null && user.getRole() != Role.CUSTOMER) {
            String message = user.getRole() == Role.STAFF
                    ? "Nhân viên dùng quầy vé tại /staff — không đặt vé như khách hàng."
                    : "Tài khoản quản trị không dùng chức năng đặt vé khách.";
            session.setAttribute("accessDeniedMessage", message);
            response.sendRedirect(user.getRole() == Role.STAFF ? "/staff" : "/home");
            return false;
        }
        return true;
    }
}
