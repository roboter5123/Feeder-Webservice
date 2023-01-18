package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.databaseobject.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, String>, CrudRepository<AccessToken, String> {

    AccessToken findByToken(String token);
}