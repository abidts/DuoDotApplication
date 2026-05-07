package com.duodot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RegistrationOtpService {

    private static final String REGISTRATION_OTP_KEY_PREFIX = "auth:registration:otp:";

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${application.security.otp.validity-minutes:5}")
    private long otpValidityMinutes;

    @Value("${application.security.otp.length:6}")
    private int otpLength;

    public String generateAndStoreOtp(String userId) {
        String otp = generateOtp();
        stringRedisTemplate.opsForValue().set(
                buildRegistrationOtpKey(userId),
                otp,
                Duration.ofMinutes(otpValidityMinutes)
        );
        return otp;
    }

    public boolean validateOtp(String userId, String otp) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(otp)) {
            return false;
        }

        String cachedOtp = getOtp(userId);
        if (!StringUtils.hasText(cachedOtp)) {
            return false;
        }
        return cachedOtp.equals(otp);
    }

    public String getOtp(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }

        return stringRedisTemplate.opsForValue().get(buildRegistrationOtpKey(userId));
    }

    public void clearOtp(String userId) {
        stringRedisTemplate.delete(buildRegistrationOtpKey(userId));
    }

    public long getOtpValidityMinutes() {
        return otpValidityMinutes;
    }

    private String generateOtp() {
        int effectiveLength = Math.max(4, otpLength);
        int lowerBound = (int) Math.pow(10, effectiveLength - 1);
        int upperBound = (int) Math.pow(10, effectiveLength) - 1;
        int otp = ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
        return String.valueOf(otp);
    }

    private String buildRegistrationOtpKey(String userId) {
        return REGISTRATION_OTP_KEY_PREFIX + userId;
    }
}
