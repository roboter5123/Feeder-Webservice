package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, CrudRepository<Schedule, Long> {
    Schedule findByScheduleId(int scheduleId);

    Schedule findByName(String name);

}
