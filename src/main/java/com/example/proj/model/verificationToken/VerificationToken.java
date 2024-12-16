package com.example.proj.model.verificationToken;

import com.example.proj.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "")
public class VerificationToken {
    private static final int EXPIRATION_TIME = 10;
    @Id
    private String id;
    private String token;
    private Date expirationTime;
    @DBRef
    private User user;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationTime = calculateExpTime();
    }

    public VerificationToken(String token) {
        this.token = token;
        this.expirationTime = calculateExpTime();
    }

    private Date calculateExpTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, VerificationToken.EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
