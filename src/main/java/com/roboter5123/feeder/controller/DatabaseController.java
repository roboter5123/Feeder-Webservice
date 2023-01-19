package com.roboter5123.feeder.controller;

import com.roboter5123.feeder.databaseobject.*;
import com.roboter5123.feeder.datasource.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class DatabaseController {

    private final FeederDao feederDao;
    private final ScheduleDao scheduleDao;
    private final TaskDao taskDao;
    private final UserDao userDao;
    private final AccessTokenRepository accessTokenRepository;
    private final DispensationRepository dispensationRepository;

    public DatabaseController(FeederDao feederDao, ScheduleDao scheduleDao, TaskDao taskDao, UserDao userDao, AccessTokenRepository accessTokenRepository,
                              DispensationRepository dispensationRepository) {

        this.feederDao = feederDao;
        this.scheduleDao = scheduleDao;
        this.taskDao = taskDao;
        this.userDao = userDao;
        this.accessTokenRepository = accessTokenRepository;
        this.dispensationRepository = dispensationRepository;
    }

    public void save(User user) {

        userDao.save(user);
    }

    public void save(AccessToken accessToken) {

        accessTokenRepository.save(accessToken);
    }

    public void save(Feeder feeder) {

        feederDao.save(feeder);
    }

    public void delete(AccessToken accessToken) {

        User user = userDao.findByAccessToken(accessToken);
        user.setAccessToken(null);
        userDao.save(user);
        accessTokenRepository.deleteById(accessToken.getToken());
    }

    public void delete(Feeder feeder) {

        List<User> users = userDao.findByFeeders_Uuid(feeder.getUuid());
        for (User user : users) {

            user.setFeeder(feeder.getUuid(), null);
            userDao.save(user);
        }
        feederDao.delete(feeder);
    }

    public void delete(Schedule schedule) {

        taskDao.deleteAll(schedule.getTasks());
        List<Feeder> feeders = feederDao.findBySchedule(schedule);
        for (Feeder feeder : feeders) {

            feeder.setSchedule(null);
        }

        feederDao.saveAll(feeders);
        scheduleDao.delete(schedule);
    }

    public User findByEmail(String email) {

        return userDao.findByEmail(email);
    }

    public User findByAccessToken(AccessToken accessToken) {

        return userDao.findByAccessToken(accessToken);
    }

    public AccessToken findByToken(String token) {

        return accessTokenRepository.findByToken(token);
    }

    public Feeder findByUuid(UUID uuid) {

        return feederDao.findByUuid(uuid);
    }

    public List <Feeder> findBySchedule(Schedule schedule){

        return feederDao.findBySchedule(schedule);
    }

    public void save(Schedule schedule) {

        scheduleDao.save(schedule);
    }

    public void save(Dispensation dispensation) {

        dispensationRepository.save(dispensation);
    }



    public void save(Task task) {

    taskDao.save(task);
    }

    public void delete(Task task) {

        taskDao.delete(task);
    }

    public Schedule findByScheduleName(String scheduleName) {

        return scheduleDao.findByName(scheduleName);
    }

    public Task findByTaskId(int taskId) {

        return taskDao.findByTaskId(taskId);
    }

    public List<User> findUsersByFeeder(Feeder feeder) {

        return userDao.findByFeeders_Uuid(feeder.getUuid());
    }
}
