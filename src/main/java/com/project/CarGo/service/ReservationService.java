package com.project.CarGo.service;

import com.project.CarGo.entity.Reservation;
import com.project.CarGo.entity.Vehicle;
import com.project.CarGo.repository.ReservationRepository;
import com.project.CarGo.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public double calculateTotalPrice(Vehicle vehicle, Reservation reservation) {
        var start = reservation.getReservationStartDate();
        var end   = reservation.getReservationEndDate();

        long days = ((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1;
        if (days <= 0) days = 1;

        return vehicle.getDailyRate().multiply(BigDecimal.valueOf(days)).doubleValue();
    }

    public boolean checkReservationOverlap(Reservation reservation) {
        Date start = reservation.getReservationStartDate();
        Date end   = reservation.getReservationEndDate();
        Vehicle vehicle = vehicleRepository.getOne(reservation.getVehicleId());

        List<Reservation> allReservationsById = reservationRepository.findAllByVehicleId(vehicle.getId());

        for (Reservation r : allReservationsById) {
            if((r.getReservationEndDate().after(start) && r.getReservationStartDate().after(end)) ||
                r.getReservationEndDate().before(start) && r.getReservationStartDate().before(end))
            {
                return false;
            }
        }
        return true;
    }
}