package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Service.OtpService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpServiceImpl implements OtpService {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public boolean verifyOtp(String enteredOtp, String storedOtp) {
        return enteredOtp != null && storedOtp != null && enteredOtp.equals(storedOtp);
    }

    @Override
    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
