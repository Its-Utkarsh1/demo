package com.LabResourceUtilizationPlatform.Service;

public interface OtpService {

    public boolean verifyOtp(String email, String inputOtp);
    public String generateOtp();
}
