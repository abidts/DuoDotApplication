package com.duodot.service;

import com.duodot.Utils.CommonUtils;
import com.duodot.constants.CommonConstants;
import com.duodot.enums.UserStatusEnum;
import com.duodot.requestBean.AuthenticationRequestBean;
import com.duodot.requestBean.ConfirmSignUpRequestBean;
import com.duodot.requestBean.RefreshTokenRequestBean;
import com.duodot.requestBean.RegisterRequestBean;
import com.duodot.responseBean.AuthenticationResponseBean;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.User;
import com.duodot.exception.BadRequestException;
import com.duodot.exception.ResourceAlreadyExistsException;
import com.duodot.repository.UserRepository;
import com.duodot.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RegistrationOtpService registrationOtpService;
    private final OtpNotificationService otpNotificationService;
    private final StringRedisTemplate stringRedisTemplate;
    
    @Transactional
    public ServiceResponseBean signUp(RegisterRequestBean request, ServiceResponseBean serviceResponseBean) {
        try {
            log.info("Request object for signup ={}", request);
            if (userRepository.existsByEmail(request.getEmail())) {
                serviceResponseBean.setMessage("Email already registered.");
                return serviceResponseBean;
            }

            if (userRepository.existsByUsername(request.getUsername())) {
                serviceResponseBean.setMessage("Username already taken.");
                return serviceResponseBean;
            }

            String userId = CommonConstants.INSTANCE.USER_PREFIX.concat(CommonUtils.INSTANCE.uniqueIdentifier());
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phoneNumber(request.getPhoneNumber())
                    .countryCode(request.getCountryCode())
                    .city(request.getCity())
                    .state(request.getState())
                    .userId(userId)
                    .createdDate(Calendar.getInstance())
                    .paired(Boolean.FALSE)
                    .pairIds(new ArrayList<>())
                    .accountStatus(UserStatusEnum.VERIFICATION_PENDING.getValue())
                    .build();

            this.userRepository.save(user);
            try {
                String otp = registrationOtpService.generateAndStoreOtp(userId);
                otpNotificationService.sendRegistrationOtp(
                        request.getEmail(),
                        request.getCountryCode(),
                        request.getPhoneNumber(),
                        otp,
                        registrationOtpService.getOtpValidityMinutes()
                );
            } catch (Exception ex) {
                registrationOtpService.clearOtp(userId);
                log.error("Failed to send signup OTP for userId={}", userId, ex);
                this.userRepository.delete(user);
                serviceResponseBean.setMessage("Unable to send OTP  to email right now. Please try again.");
                return serviceResponseBean;
            }

            AuthenticationResponseBean authenticationResponseBean = AuthenticationResponseBean.builder()
                    .userId(userId)
                    .accountStatus(UserStatusEnum.VERIFICATION_PENDING.getValue())
                    .build();

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("OTP sent successfully to email. OTP is valid for "
                    + registrationOtpService.getOtpValidityMinutes() + " minutes.");
            serviceResponseBean.setData(authenticationResponseBean);
        }catch(Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());

        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean confirmSignUp(ConfirmSignUpRequestBean request, ServiceResponseBean serviceResponseBean) {

        try{
            User user = userRepository.findByUserId(request.getUserId());
           if(Objects.nonNull(user)){
               if (UserStatusEnum.ACTIVE.getValue().equalsIgnoreCase(user.getAccountStatus())) {
                   serviceResponseBean.setMessage("Signup is already confirmed");
                   return serviceResponseBean;
               }
               if (!this.registrationOtpService.validateOtp(request.getUserId(), request.getOtp())) {
                   serviceResponseBean.setMessage("Invalid or expired OTP");
                   return serviceResponseBean;
               }
               user.setAccountStatus(UserStatusEnum.ACTIVE.getValue());
               user.setUpdatedDate(Calendar.getInstance());
               this.userRepository.save(user);
               this.registrationOtpService.clearOtp(request.getUserId());
               serviceResponseBean.setStatus(Boolean.TRUE);
               serviceResponseBean.setMessage("Signup confirmed successfully");
           } else{
               serviceResponseBean.setMessage("User not found.");
           }
        }catch(Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }

        return serviceResponseBean;
    }
    
    public ServiceResponseBean signIn(AuthenticationRequestBean request, ServiceResponseBean serviceResponseBean) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            serviceResponseBean.setMessage("Invalid email or password.");
            return serviceResponseBean;
        }
        
        var user = userRepository.findByEmail(request.getEmail());
        if(Objects.nonNull(user)){
            if (!UserStatusEnum.ACTIVE.getValue().equalsIgnoreCase(user.getAccountStatus())) {
                throw new BadRequestException("Account is not active. Please complete OTP verification first.");
            }

            var userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    java.util.Collections.emptyList()
            );

            var jwtToken = jwtService.generateToken(userDetails);
            var refreshToken = jwtService.generateRefreshToken(userDetails);

            stringRedisTemplate.opsForValue().set(
                    CommonConstants.INSTANCE.REFRESH_TOKEN_KEY_PREFIX + user.getUserId(),
                    refreshToken,
                    Duration.ofDays(CommonConstants.INSTANCE.REFRESH_TOKEN_VALIDITY_DAYS)
            );

            AuthenticationResponseBean authenticationResponseBean = AuthenticationResponseBean.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .userId(user.getUserId())
                    .build();

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Login successful");
            serviceResponseBean.setData(authenticationResponseBean);

        }else{
            serviceResponseBean.setMessage("User not found.");
        }

        return serviceResponseBean;
    }

    public ServiceResponseBean signOut(String userId, ServiceResponseBean serviceResponseBean) {
        try {
            String redisKey = CommonConstants.INSTANCE.REFRESH_TOKEN_KEY_PREFIX + userId;
            stringRedisTemplate.delete(redisKey);
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Sign out successful");
        } catch (Exception e) {
            log.error("Exception during sign out for userId={} :: {}", userId, e);
            serviceResponseBean.setMessage("Sign out failed: " + e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    public ServiceResponseBean refreshToken(RefreshTokenRequestBean request, ServiceResponseBean serviceResponseBean) {
        try {
            var user = userRepository.findByUserId(request.getUserId());
            if (Objects.isNull(user)) {
                serviceResponseBean.setMessage("User not found.");
                return serviceResponseBean;
            }
            String redisKey = CommonConstants.INSTANCE.REFRESH_TOKEN_KEY_PREFIX + request.getUserId();
            String storedRefreshToken = stringRedisTemplate.opsForValue().get(redisKey);

            if (!StringUtils.hasText(storedRefreshToken)) {
                serviceResponseBean.setMessage("Refresh token not found or expired.");
                return serviceResponseBean;
            }
            if (!storedRefreshToken.equals(request.getRefreshToken())) {
                serviceResponseBean.setMessage("Invalid refresh token.");
                return serviceResponseBean;
            }

            var userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    java.util.Collections.emptyList()
            );

            var newAccessToken = jwtService.generateToken(userDetails);

            AuthenticationResponseBean authenticationResponseBean = AuthenticationResponseBean.builder()
                    .accessToken(newAccessToken).refreshToken(request.getRefreshToken())
                    .build();

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Access token refreshed successfully");
            serviceResponseBean.setData(authenticationResponseBean);
        } catch (Exception e) {
            log.error("Exception during refresh token for userId={} :: {}", request.getUserId(), e);
            serviceResponseBean.setMessage("Token refresh failed: " + e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }
}
