package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.databaseobject.Feeder;
import com.roboter5123.feeder.databaseobject.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeederDao extends JpaRepository<Feeder, Long> {
    List<Feeder> findBySchedule(Schedule schedule);

    Feeder findByUuid(UUID uuid);
}
