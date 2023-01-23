package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CrudRepository<User, Long> {

    List<User> findByFeeders_Uuid(UUID uuid);

    User findByAccessToken(AccessToken accessToken);

    User findByEmail(String email);
}
