package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.LoginRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.RefreshTokenRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.ResendOtpRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.VerifyEmailRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    void verifyEmail(VerifyEmailRequest request);

    void resendOtp(ResendOtpRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
