package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.Vehicle;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    private final VehicleRepository vehicleRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(VehicleRepository vehicleRepository, ReservationRepository reservationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.reservationRepository= reservationRepository;
    }

    public BigDecimal calculateTotalPrice(Vehicle vehicle, Reservation reservation) {

        Date start = reservation.getReservationStartDate();
        Date end   = reservation.getReservationEndDate();
        BigDecimal dailyRate = vehicle.getDailyRate();
        LocalDate s = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate e = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long days = ChronoUnit.DAYS.between(s, e);
        if (days <= 0) days = 1;
        return dailyRate.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean checkReservationOverlap(Reservation reservation) {
        return reservationRepository.countOverlaps(
                reservation.getVehicleId(),
                reservation.getReservationStartDate(),
                reservation.getReservationEndDate(),
                reservation.getId()
        ) > 0;
    }
}