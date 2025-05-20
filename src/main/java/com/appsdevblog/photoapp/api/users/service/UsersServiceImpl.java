package com.appsdevblog.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdevblog.photoapp.api.users.data.UserEntity;
import com.appsdevblog.photoapp.api.users.data.UsersRepository;
import com.appsdevblog.photoapp.api.users.shared.UserDto;

@Service
public class UsersServiceImpl implements UsersService {

	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
    @Override
    public UserDto createUser(UserDto userDetails) {
        // TODO Auto-generated method stub
       
        userDetails.setUserId(UUID.randomUUID().toString());
      
        // Encriptacion de clave de usuario
        String password = userDetails.getPassword();
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        userDetails.setEncryptedPassword(encryptedPassword);
        
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        
        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
        
        //userEntity.setEncryptedPassword("test");   // para pruebas sin Spring Security

        UserEntity userCreated = usersRepository.save(userEntity);
        
        UserDto returnUser = modelMapper.map(userCreated, UserDto.class);
        
        return returnUser;
        
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

        UserEntity userEntity = usersRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        // Convertimos el objeto UserEntity a UserDetails (mediante User)
        // ----> org.springframework.security.core.userdetails.User
        UserDetails userDetails = new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                true, // enabled
                new ArrayList<>() // granted authorities
        );

        return userDetails;

    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        // TODO Auto-generated method stub

        UserEntity user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);  
        UserDto userDto = modelMapper.map(user, UserDto.class);
        
        return userDto;
        
    }
    
}
