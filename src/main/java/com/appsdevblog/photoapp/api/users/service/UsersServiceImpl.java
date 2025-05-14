package com.appsdevblog.photoapp.api.users.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdevblog.photoapp.api.users.data.UserEntity;
import com.appsdevblog.photoapp.api.users.data.UsersRepository;
import com.appsdevblog.photoapp.api.users.shared.UserDto;

@Service
public class UsersServiceImpl implements UsersService {

	UsersRepository usersRepository;
	
	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}
	
    @Override
    public UserDto createUser(UserDto userDetails) {
        // TODO Auto-generated method stub
       
        userDetails.setUserId(UUID.randomUUID().toString());
        
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        
        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
        
        userEntity.setEncryptedPassword("test");   // para pruebas sin Spring Security

        UserEntity userCreated = usersRepository.save(userEntity);
        
        UserDto returnUser = modelMapper.map(userCreated, UserDto.class);
        
        return returnUser;
        
    }
    
}
