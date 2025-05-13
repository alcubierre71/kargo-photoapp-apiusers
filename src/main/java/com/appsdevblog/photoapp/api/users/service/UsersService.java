package com.appsdevblog.photoapp.api.users.service;

import com.appsdevblog.photoapp.api.users.shared.UserDto;

public interface UsersService {

    UserDto createUser(UserDto userDetails);
    
}
