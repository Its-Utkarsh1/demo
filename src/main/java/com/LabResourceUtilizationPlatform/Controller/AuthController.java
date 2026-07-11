package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Request.*;
import com.LabResourceUtilizationPlatform.Dtos.Response.AuthResponse;
import com.LabResourceUtilizationPlatform.Service.ServiceImpl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {

        authService.verifyEmail(request);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody ResendOtpRequest request) {

        authService.resendOtp(request);
        return ResponseEntity.ok("OTP sent successfully.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        return ResponseEntity.ok("OTP sent successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok("Password updated successfully.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }
}
