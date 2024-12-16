package com.example.proj.service.user;

import com.example.proj.dto.user.PasswordDTO;
import com.example.proj.dto.user.UserDTO;
import com.example.proj.model.passwordResetToken.PasswordResetToken;
import com.example.proj.model.user.User;
import com.example.proj.model.verificationToken.VerificationToken;
import com.example.proj.repository.PasswordResetTokenRepository;
import com.example.proj.repository.UserRepository;
import com.example.proj.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        User userRegi = new User();
        userRegi.setFirstName(userDTO.getFirstName());
        userRegi.setLastName(userDTO.getLastName());
        userRegi.setEmail(userDTO.getEmail());
        userRegi.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRegi.setRole("USER");
        return userRepository.save(userRegi);
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(token,user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateRegistrationToken(String token) {
        VerificationToken tokenBy = verificationTokenRepository.findByToken(token);
        if(tokenBy == null){
            return "Invalid";
        }

        Calendar calendar = Calendar.getInstance();
        if(tokenBy.getExpirationTime() != null && tokenBy.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            // Here you cant delete the token since you need to resend it
            //verificationTokenRepository.delete(tokenBy);
            return "Expired";
        }

        User user = tokenBy.getUser();
        user.setEnable(true);
        userRepository.save(user);
        return "";
    }

    @Override
    public String generateNewTokenUrl(String oldToken) {
        VerificationToken token = verificationTokenRepository.findByToken(oldToken);
        if(token!=null){
            token.setToken(UUID.randomUUID().toString());
            verificationTokenRepository.save(token);
            return token.getToken();
        }else{
            return "";
        }
    }

    @Override
    public User resetPasswordByEmail(PasswordDTO passwordDTO) {
        return userRepository.findUserByEmail(passwordDTO.getEmail());
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token,user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String passwordToken) {
        PasswordResetToken tokenBy = passwordResetTokenRepository.findByToken(passwordToken);
        if(tokenBy == null){
            return "Invalid";
        }

        Calendar calendar = Calendar.getInstance();
        if(tokenBy.getExpirationTime() != null && tokenBy.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            return "Expired";
        }
        return "";
    }

    @Override
    public User getUserByPasswordResetToken(String passwordToken) {
        return passwordResetTokenRepository.findByToken(passwordToken).getUser();
    }

    @Override
    public void resetUserPassword(User user, PasswordDTO passwordDTO) {
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String findUserByEmailAndOldPassword(PasswordDTO passwordDTO) {
        User userByEmail = userRepository.findUserByEmail(passwordDTO.getEmail());
        if(userByEmail != null){
            if(passwordEncoder.matches(passwordDTO.getOldPassword(), userByEmail.getPassword())){
                userByEmail.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
                userRepository.save(userByEmail);
                return "";
            }else{
                return "Invalid Old Password";
            }
        }else{
            return "Invalid User Email";
        }
    }
}
