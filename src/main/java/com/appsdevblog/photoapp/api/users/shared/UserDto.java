package com.appsdevblog.photoapp.api.users.shared;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * UserDto is a Data Transfer Object used to transfer user-related data
 * between different layers of the application. It includes fields for
 * user details such as first name, last name, email, and more.
 */
public class UserDto implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;

    private String firstName;
   
    private String lastName;
  
    private String password;
 
    private String email;
    private String userId;
    private String encryptedPassword;

}
