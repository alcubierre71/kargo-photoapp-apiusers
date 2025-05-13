package com.appsdevblog.photoapp.api.users.service;

import java.util.UUID;

import com.appsdevblog.photoapp.api.users.shared.UserDto;

public class UsersServiceImpl implements UsersService {

    @Override
    public UserDto createUser(UserDto userDetails) {
        // TODO Auto-generated method stub
       
        userDetails.setUserId(UUID.randomUUID().toString());

    }
    
}
