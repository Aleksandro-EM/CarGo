package com.project.CarGo.repository;

import com.project.CarGo.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByNumberPlate(String licensePlate);
    Vehicle findByNumberPlate(String licensePlate);

    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN (" +
            "SELECT r.vehicleId FROM Reservation r " +
            "WHERE r.reservationStartDate <= :endDate AND r.reservationEndDate >= :startDate)" +
            "AND (:categoryId IS NULL OR v.category.id = :categoryId)")
    List<Vehicle> findAvailableVehiclesByDateAndCategory(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("categoryId") Long categoryId);
}
