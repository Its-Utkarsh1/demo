package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.*;
import com.LabResourceUtilizationPlatform.Dtos.Response.AuthResponse;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Security.JwtUtils;
import com.LabResourceUtilizationPlatform.Service.AuthService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final OtpServiceImpl  otpService;
    private final EmailServiceImpl emailService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found."));

        //Check if User is Verified or not
        if(!user.getEmailVerified()){
            throw new RuntimeException("Please verify your email first.");
        }

        //Authenticating
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        //Loading authenticated user
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        //Generate tokens
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        logger.info("User logged in successfully: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName().name())
                .institutionName(user.getInstitution().getName())
                .emailVerified(user.getEmailVerified())
                .build();
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Check if already verified
        if (user.getEmailVerified()) {
            throw new RuntimeException("Email is already verified.");
        }

        // Get OTP from Redis
        String storedOtp = redisTemplate.opsForValue()
                .get("otp:" + request.getEmail());

        // OTP expired or not found
        if (storedOtp == null) {
            throw new RuntimeException("OTP has expired.");
        }

        // Verify OTP
        if (!storedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        // Mark email as verified
        user.setEmailVerified(true);
        userRepository.save(user);

        // Remove OTP from Redis
        redisTemplate.delete("otp:" + request.getEmail());

        logger.info("Email verified successfully: {}", user.getEmail());
    }

    @Override
    public void resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (user.getEmailVerified()) {
            throw new RuntimeException("Email is already verified.");
        }

        String otp = otpService.generateOtp();

        // Store new OTP in Redis for 10 minutes
        redisTemplate.opsForValue().set(
                "otp:" + user.getEmail(),
                otp,
                Duration.ofMinutes(10)
        );
        emailService.sendOtpByEmail(user.getEmail(), otp);

        logger.info("OTP resent successfully to {}", user.getEmail());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        String otp = otpService.generateOtp();

        redisTemplate.opsForValue().set(
                "reset-otp:" + user.getEmail(),
                otp,
                Duration.ofMinutes(10)
        );

        emailService.sendOtpByEmail(user.getEmail(), otp);

        logger.info("Password reset OTP sent to {}", user.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        String storedOtp = redisTemplate.opsForValue()
                .get("reset-otp:" + request.getEmail());

        if (storedOtp == null) {
            throw new RuntimeException("OTP has expired.");
        }

        if (!storedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        redisTemplate.delete("reset-otp:" + request.getEmail());

        logger.info("Password reset successfully for {}", user.getEmail());
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        if (!jwtUtils.validateRefreshToken(request.getRefreshToken())) {
            throw new RuntimeException("Invalid or expired refresh token.");
        }

        String email = jwtUtils.getUsernameFromRefreshToken(
                request.getRefreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

        String newAccessToken =
                jwtUtils.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName().name())
                .institutionName(user.getInstitution().getName())
                .emailVerified(user.getEmailVerified())
                .build();
    }
}
