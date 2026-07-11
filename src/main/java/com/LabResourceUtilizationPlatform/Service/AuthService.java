package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.*;
import com.LabResourceUtilizationPlatform.Dtos.Response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    void verifyEmail(VerifyEmailRequest request);

    void resendOtp(ResendOtpRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
