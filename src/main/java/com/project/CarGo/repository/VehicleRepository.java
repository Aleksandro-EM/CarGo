package com.project.CarGo.repository;

import com.project.CarGo.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByNumberPlate(String licensePlate);
    Vehicle findByNumberPlate(String licensePlate);
}
