package com.LabResourceUtilizationPlatform.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

//    private final AuthServiceImpl authService;
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(
//            @Valid @RequestBody LoginRequest request) {
//
//        return ResponseEntity.ok(authService.login(request));
//    }
//
//    @PostMapping("/verify-email")
//    public ResponseEntity<String> verifyEmail(
//            @Valid @RequestBody VerifyEmailRequest request) {
//
//        authService.verifyEmail(request);
//        return ResponseEntity.ok("Email verified successfully.");
//    }
//
//    @PostMapping("/resend-otp")
//    public ResponseEntity<String> resendOtp(
//            @Valid @RequestBody ResendOtpRequest request) {
//
//        authService.resendOtp(request);
//        return ResponseEntity.ok("OTP sent successfully.");
//    }
}
