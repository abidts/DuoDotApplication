package com.duodot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpNotificationService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${application.notification.email.resend.api-key:}")
    private String resendApiKey;

    @Value("${application.notification.email.resend.url:https://api.resend.com/emails}")
    private String resendApiUrl;

    @Value("${application.notification.email.from:}")
    private String notificationFromEmail;

    public void sendRegistrationOtp(String email, String countryCode, String phoneNumber, String otp, long validityMinutes) {
        // Phone OTP delivery intentionally disabled; OTP is sent via email only (Resend).
        sendOtpToEmail(email, otp, validityMinutes);
    }

    private void sendOtpToEmail(String email, String otp, long validityMinutes) {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new IllegalStateException("Resend API key is missing. Configure application.notification.email.resend.api-key");
        }
        if (notificationFromEmail == null || notificationFromEmail.isBlank()) {
            throw new IllegalStateException("Email sender address is missing. Configure application.notification.email.from");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", notificationFromEmail);
        payload.put("to", List.of(email));
        payload.put("subject", "DuoDot Registration OTP");
        payload.put("text", "Your DuoDot OTP is " + otp + ". It will expire in " + validityMinutes + " minutes.");

        try {
            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(resendApiUrl))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Resend email send failed. status={}, body={}", response.statusCode(), response.body());
                throw new IllegalStateException("Failed to send OTP email via Resend");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to create Resend email payload", e);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error while sending OTP email via Resend", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("OTP email send interrupted", e);
        }
    }
}
