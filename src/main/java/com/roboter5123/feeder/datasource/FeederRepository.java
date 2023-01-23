package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.model.Feeder;
import com.roboter5123.feeder.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeederRepository extends JpaRepository<Feeder, Long> {
    List<Feeder> findBySchedule(Schedule schedule);

    Feeder findByUuid(UUID uuid);
}
