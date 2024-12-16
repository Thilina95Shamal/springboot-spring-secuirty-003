package com.example.proj.component.listener;

import com.example.proj.event.RegistrationCompleteEvent;
import com.example.proj.model.user.User;
import com.example.proj.service.user.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;

    public RegistrationCompleteEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create Verification Token for the User
        User user = event.getUser();
        String token  = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);
        // Send Mail to User
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;
            // The below should be the email body
        System.out.println("verifcation URl " + url);
    }
}
