package com.appsdevblog.photoapp.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestModel {

    @NotNull(message = "First Name cannot be null")
    @Size(min=2, message = "First Name must be at least 2 characters")
    private String firstName;
    @NotNull(message = "Last Name cannot be null")
    @Size(min=2, message = "Last Name must be at least 2 characters")
    private String lastName;
    @NotNull(message = "Password cannot be null")
    @Size(min=8, max=16, message = "Password must be at least 8 characters")
    private String password;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email is not valid")
    private String email;

}
