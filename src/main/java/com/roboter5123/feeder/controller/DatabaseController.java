package com.roboter5123.feeder.controller;

import com.roboter5123.feeder.model.*;
import com.roboter5123.feeder.datasource.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

/**
 * Database Controller is a wrapper class around the JPA Repositories for the API Model. This makes it easier to save all kinds of objects by providing a single object to use
 * @author roboter5123
 */
@Controller
public class DatabaseController {

    private final FeederRepository feederRepository;
    private final ScheduleRepository scheduleRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final DispensationRepository dispensationRepository;

    public DatabaseController(FeederRepository feederRepository, ScheduleRepository scheduleRepository, TaskRepository taskRepository, UserRepository userRepository, AccessTokenRepository accessTokenRepository,
                              DispensationRepository dispensationRepository) {

        this.feederRepository = feederRepository;
        this.scheduleRepository = scheduleRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.dispensationRepository = dispensationRepository;
    }


    public void save(User user) {

        userRepository.save(user);
    }

    public void save(AccessToken accessToken) {

        accessTokenRepository.save(accessToken);
    }

    public void save(Feeder feeder) {

        feederRepository.save(feeder);
    }

    public void delete(AccessToken accessToken) {

        User user = userRepository.findByAccessToken(accessToken);
        user.setAccessToken(null);
        userRepository.save(user);
        accessTokenRepository.deleteById(accessToken.getToken());
    }

    public void delete(Feeder feeder) {

        List<User> users = userRepository.findByFeedersUuid(feeder.getUuid());

        for (User user : users) {

            user.addFeeder(feeder.getUuid(), null);
            userRepository.save(user);
        }

        feederRepository.delete(feeder);
    }

    public void delete(Schedule schedule) {

        taskRepository.deleteAll(schedule.getTasks());
        List<Feeder> feeders = feederRepository.findBySchedule(schedule);
        for (Feeder feeder : feeders) {

            feeder.setSchedule(null);
        }

        feederRepository.saveAll(feeders);
        scheduleRepository.delete(schedule);
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public User findByAccessToken(AccessToken accessToken) {

        return userRepository.findByAccessToken(accessToken);
    }

    public AccessToken findByToken(String token) {

        return accessTokenRepository.findByToken(token);
    }

    public Feeder findByUuid(UUID uuid) {

        return feederRepository.findByUuid(uuid);
    }

    public List <Feeder> findBySchedule(Schedule schedule){

        return feederRepository.findBySchedule(schedule);
    }

    public User findByLoginData(LoginData loginData){

        return userRepository.findByEmail(loginData.getEmail());
    }

    public void save(Schedule schedule) {

        scheduleRepository.save(schedule);
    }

    public void save(Dispensation dispensation) {

        dispensationRepository.save(dispensation);
    }

    public void save(Task task) {

    taskRepository.save(task);
    }

    public void delete(Task task) {

        try{
        taskRepository.delete(task);}catch (Exception e){

            e.printStackTrace();
        }
    }

    public Schedule findByScheduleName(String scheduleName) {

        return scheduleRepository.findByName(scheduleName);
    }

    public Task findByTaskId(int taskId) {

        return taskRepository.findByTaskId(taskId);
    }

    public List<User> findUsersByFeeder(Feeder feeder) {

        return userRepository.findByFeedersUuid(feeder.getUuid());
    }
}
