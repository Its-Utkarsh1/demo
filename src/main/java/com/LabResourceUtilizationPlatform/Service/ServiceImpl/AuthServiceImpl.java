package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.LoginRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.ResendOtpRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.VerifyEmailRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.AuthResponse;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Security.JwtUtils;
import com.LabResourceUtilizationPlatform.Service.AuthService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found."));

        //Verify is user is already verified
        if(user.getEmailVerified()){
            throw  new RuntimeException("Email is already verified.");
        }

        if(user.getOtpExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Otp has expired.");
        }

        //Verifying Otp
        if(!otpService.verifyOtp(request.getOtp(), user.getOtp())){
            throw new RuntimeException("Invalid Otp.");
        }


        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
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

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);

        emailService.sendOtpByEmail(user.getEmail(), otp);

        logger.info("OTP resent successfully to {}", user.getEmail());
    }
}
