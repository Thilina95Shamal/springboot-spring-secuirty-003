package com.example.proj.controller;

import com.example.proj.dto.user.PasswordDTO;
import com.example.proj.dto.user.UserDTO;
import com.example.proj.event.RegistrationCompleteEvent;
import com.example.proj.model.user.User;
import com.example.proj.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;


@RestController
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RegistrationController(UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/register")
    private ResponseEntity<?> register(@RequestBody UserDTO userDTO, HttpServletRequest servletRequest) {
        User user = userService.registerUser(userDTO);
        applicationEventPublisher.publishEvent(
                new RegistrationCompleteEvent(
                        user,
                        "http://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort()));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/verifyRegistration")
    private ResponseEntity<?> verifyRegistration(@RequestParam String token) {
        String result = userService.validateRegistrationToken(token);
        if (result.isBlank()) {
            return new ResponseEntity<>("User Verification Successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/resendToken")
    private ResponseEntity<?> resendToken(@RequestParam String oldToken, HttpServletRequest servletRequest) {
        String newToken = userService.generateNewTokenUrl(oldToken);
        //You have to send the Email intsead of printing
        if (!newToken.isBlank()) {
            String newUrlToken = "http://"
                    + servletRequest.getServerName()
                    + ":" + servletRequest.getServerPort()
                    + "/verifyRegistration?token="
                    + newToken;
            System.out.println(newUrlToken);
            return new ResponseEntity<>("Resend Successful", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid Token", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/resetPassword")
    private ResponseEntity<?> resetPassword(@RequestBody PasswordDTO passwordDTO, HttpServletRequest servletRequest) {
        User user = userService.resetPasswordByEmail(passwordDTO);

        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetToken(user, token);
            //You have to send the Email instead of printing
            String newUrlToken = "http://"
                    + servletRequest.getServerName()
                    + ":" + servletRequest.getServerPort()
                    + "/savePassword?token="
                    + token;
            System.out.println("Click here to reset your Password :" + newUrlToken);
            return new ResponseEntity<>("Email Sent To Reset The Password", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/savePassword")
    private ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody PasswordDTO passwordDTO) {
        String result = userService.validatePasswordResetToken(token);
        User user = userService.getUserByPasswordResetToken(token);
        if (result.isBlank() && user!=null) {
            userService.resetUserPassword(user,passwordDTO);
            return new ResponseEntity<>("User Password Reset Successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changePassword")
    private ResponseEntity<?> changePassword(@RequestBody PasswordDTO passwordDTO) {
        String result = userService.findUserByEmailAndOldPassword(passwordDTO);
        if (result.isBlank()) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
