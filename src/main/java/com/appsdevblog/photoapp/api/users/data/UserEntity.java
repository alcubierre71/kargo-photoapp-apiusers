package com.appsdevblog.photoapp.api.users.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
  
    @Column(name = "email", unique = true,  length = 120)
    private String email;
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    //private String password;
    @Column(name = "encrypted_password", nullable = false, unique = true)
    private String encryptedPassword;

}
