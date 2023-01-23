package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, CrudRepository<Task, Long> {

    Task findByTaskId(int taskId);

}
