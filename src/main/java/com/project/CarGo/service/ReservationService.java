package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.Vehicle;
import com.project.CarGo.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final VehicleRepository vehicleRepository;

    public ReservationService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public double calculateTotalPrice(Vehicle vehicle, Reservation reservation) {
        var start = reservation.getReservationStartDate();
        var end   = reservation.getReservationEndDate();

        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        if (days <= 0) days = 1;

        return vehicle.getDailyRate().multiply(java.math.BigDecimal.valueOf(days)).doubleValue();
    }
}