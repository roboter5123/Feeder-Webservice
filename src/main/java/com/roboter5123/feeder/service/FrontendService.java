package com.roboter5123.feeder.service;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.model.*;
import com.roboter5123.feeder.util.Weekday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class FrontendService {
    DatabaseController databaseController;

    @Autowired
    public FrontendService(DatabaseController databaseController) {

        this.databaseController = databaseController;
    }

    @GetMapping("/")
    public String getIndex(@CookieValue(name = "access-token", required = false) AccessToken accessToken) {

        if (accessToken != null && databaseController.findByAccessToken(accessToken) != null) {

            return "redirect:/dashboard";
        } else {

            return "redirect:/login";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin() {

        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegister() {

        return "register";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getDashboard(@CookieValue(name = "access-token", required = false) AccessToken accessToken, Model model) {

        if (accessToken != null && databaseController.findByAccessToken(accessToken) != null) {

            User user = databaseController.findByAccessToken(accessToken);
            model.addAttribute("user", user);
            return "dashboard";

        } else {

            return "redirect:/login";
        }
    }

    @GetMapping("/feeder/{uuid}")
    public String getFeeder(@CookieValue(name = "access-token", required = false) AccessToken accessToken, @PathVariable UUID uuid, Model model) {

        if (accessToken != null && databaseController.findByAccessToken(accessToken) != null) {

            Feeder feeder = databaseController.findByUuid(uuid);
            model.addAttribute(feeder);
            User user = databaseController.findByAccessToken(accessToken);
            Schedule currentSchedule = feeder.getSchedule();

            if (currentSchedule == null) {

                currentSchedule = new Schedule("-----");
                user.addSchedule(currentSchedule);

            } else if (!user.getSchedules().contains(currentSchedule)) {

                user.addSchedule(currentSchedule);
            }

            model.addAttribute("currentSchedule", currentSchedule);
            model.addAttribute("schedules", user.getSchedules());
            List<Dispensation> dispensations = feeder.getDispensations();
            Collections.sort(dispensations);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            model.addAttribute("timeFormatter", timeFormatter);
            model.addAttribute("dispensations", dispensations);
            return "feeder";

        } else {

            return "redirect:/login";
        }
    }

    @GetMapping("/schedule/{name}")
    public String getFeeder(@CookieValue(name = "access-token", required = false) AccessToken accessToken, @PathVariable String name, Model model) {

        if (accessToken != null && databaseController.findByAccessToken(accessToken) != null) {
            User user = databaseController.findByAccessToken(accessToken);
            Schedule schedule = user.getSchedule(name);
            model.addAttribute(schedule);
            LinkedHashMap<String, List<Task>> days = new LinkedHashMap<>();

            for (int i = 0; i < 8; i++) {

                String weekday = Weekday.values()[i].name();
                days.put(weekday, new ArrayList<>());
            }

            for (Task task : schedule.getTasks()) {


                days.get(task.getWeekday().name()).add(task);
            }

            for (String s : days.keySet()) {

                Collections.sort(days.get(s));
            }

            model.addAttribute("days", days);
            return "schedule";

        } else {

            return "redirect:/login";
        }
    }

    @GetMapping("/{token}/verify")
    public String verifyAccount(@PathVariable String token, Model model) {

        AccessToken accessToken = databaseController.findByToken(token);
        User user = databaseController.findByAccessToken(accessToken);

        if (user == null || user.getActivated()) {

            return "redirect:/login";

        } else {

            user.setActivated(true);
            databaseController.delete(accessToken);
            databaseController.save(user);
            model.addAttribute("valid", true);
            user.setActivated(true);
        }

        return "verification";
    }

    @GetMapping("/{token}/resetPassword")
    public String resetPassword(@PathVariable AccessToken token, Model model) {

        User user = databaseController.findByAccessToken(token);

        if (user == null || user.getActivated()) {

            return "redirect:/login";
        } else {

            model.addAttribute("token", token.getToken());
            return "resetPassword";
        }
    }

    @GetMapping("/resetPassword")
    public String resetPassword() {

        return "forgottenPassword";
    }}
