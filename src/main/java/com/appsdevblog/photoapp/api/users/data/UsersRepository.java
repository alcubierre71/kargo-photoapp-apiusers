package com.appsdevblog.photoapp.api.users.data;

import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<UserEntity, Long> {
    
    //UserEntity findByEmail(String email);
    //UserEntity findByUserId(String userId);
    //UserEntity findByEncryptedPassword(String encryptedPassword);

    UserEntity findByEmail(String email);

}
