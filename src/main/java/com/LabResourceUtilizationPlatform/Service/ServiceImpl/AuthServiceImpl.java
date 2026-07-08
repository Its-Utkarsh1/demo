package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.LoginRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.ResendOtpRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.VerifyEmailRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.AuthResponse;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found."));

        //Check if User is Verified or not
        if(!user.getEmailVerified()){
            throw new RuntimeException("Please verify your email first.");
        }
        return null;
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {

    }

    @Override
    public void resendOtp(ResendOtpRequest request) {

    }
}
