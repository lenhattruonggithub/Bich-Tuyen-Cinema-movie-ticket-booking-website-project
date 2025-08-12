package com.example.jav_projecto1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendBookingConfirmation(String to, String movieName, String scheduleShow, String seat) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com"); // Thay bằng email của bạn
        message.setTo(to);
        message.setSubject("Xác nhận đặt vé thành công - Rạp phim Bích Tuyền");
        message.setText(String.format(
            "Xin chào,\n\n" +
            "Cảm ơn bạn đã đặt vé tại rạp phim của chúng tôi.\n\n" +
            "Thông tin đặt vé:\n" +
            "- Phim: %s\n" +
            "- Suất chiếu: %s\n" +
            "- Ghế: %s\n\n" +
            "Chúc bạn xem phim vui vẻ!\n\n" +
            "Trân trọng,\n" +
            "Rạp phim Bích Tuyền",
            movieName, scheduleShow, seat
        ));
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending booking confirmation: " + e.getMessage());
        }
    }

    @Async
    public void sendCancelBooking(String to, String movieName, String scheduleShow, String seat) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com"); // Thay bằng email của bạn
        message.setTo(to);
        message.setSubject("Thông báo hủy vé - Rạp phim Bích Tuyền");
        message.setText(String.format(
            "Xin chào,\n\n" +
            "Bạn đã hủy vé thành công tại rạp phim của chúng tôi.\n\n" +
            "Thông tin vé đã hủy:\n" +
            "- Phim: %s\n" +
            "- Suất chiếu: %s\n" +
            "- Ghế: %s\n\n" +
            "Điểm thưởng đã được hoàn lại vào tài khoản của bạn (1 điểm/1.000đ).\n\n" +
            "Trân trọng,\n" +
            "Rạp phim Bích Tuyền",
            movieName, scheduleShow, seat
        ));
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending cancel booking: " + e.getMessage());
        }
    }

    @Async
    public void sendForgotPasswordEmail(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bichtuyencinema@gmail.com");
        message.setTo(to);
        message.setSubject("Thông tin tài khoản - Rạp phim Bích Tuyền");
        message.setText(String.format(
            "Xin chào,\n\n" +
            "Bạn đã yêu cầu lấy lại thông tin tài khoản.\n\n" +
            "Thông tin tài khoản của bạn:\n" +
            "- Tên đăng nhập: %s\n" +
            "- Mật khẩu: %s\n\n" +
            "Vui lòng đăng nhập và thay đổi mật khẩu để bảo mật tài khoản.\n\n" +
            "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.\n\n" +
            "Trân trọng,\n" +
            "Rạp phim Bích Tuyền",
            username, password
        ));
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending forgot password email: " + e.getMessage());
        }
    }

    @Async
    public void sendVerifyCodeEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bichtuyencinema@gmail.com");
        message.setTo(to);
        message.setSubject("Mã xác nhận đăng ký tài khoản - Rạp phim Bích Tuyền");
        message.setText(String.format(
            "Xin chào,\n\nMã xác nhận đăng ký tài khoản của bạn là: %s\n\nMã này có hiệu lực trong 5 phút.\n\nTrân trọng,\nRạp phim Bích Tuyền", code
        ));
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending verify code email: " + e.getMessage());
        }
    }

    public String generateAndSendVerifyCode(String email, HttpServletRequest request) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        HttpSession session = request.getSession(true); // create if missing
        session.setAttribute("verifyEmail", email);
        session.setAttribute("verifyCode", code);
        session.setMaxInactiveInterval(5 * 60); // 5 min

        sendVerifyCodeEmail(email, code);

        return code;
    }
}