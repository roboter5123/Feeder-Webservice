package com.roboter5123.feeder.datasource;
import com.roboter5123.feeder.databaseobject.Dispensation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispensationRepository extends JpaRepository<Dispensation, Long> {

}