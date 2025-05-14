package com.appsdevblog.photoapp.api.users.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para exponer datos del usuario a traves de las API del proyecto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponseModel {

    private String firstName;
    private String lastName;
    //private String password;
    private String email;
    private String userId;
    //private String encryptedPassword;
	
}
