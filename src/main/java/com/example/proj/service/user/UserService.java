package com.example.proj.service.user;

import com.example.proj.dto.user.PasswordDTO;
import com.example.proj.dto.user.UserDTO;
import com.example.proj.model.user.User;

public interface UserService {
    User registerUser(UserDTO userDTO);

    void saveVerificationTokenForUser(String token, User user);

    String validateRegistrationToken(String token);

    String generateNewTokenUrl(String oldToken);

    User resetPasswordByEmail(PasswordDTO passwordDTO);

    void createPasswordResetToken(User user,String token);

    String validatePasswordResetToken(String passwordToken);

    User getUserByPasswordResetToken(String passwordToken);

    void resetUserPassword(User user, PasswordDTO passwordDTO);

    String findUserByEmailAndOldPassword(PasswordDTO passwordDTO);
}
