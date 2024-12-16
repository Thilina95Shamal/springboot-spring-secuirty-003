package com.example.proj.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {

    private String email;
    private String oldPassword;
    private String newPassword;

}
