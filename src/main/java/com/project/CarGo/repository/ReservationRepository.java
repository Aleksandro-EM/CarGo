package com.project.CarGo.repository;


import com.project.CarGo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r left join fetch r.user")
    List<Reservation> findAllWithUser();

    @Query(value = """
  SELECT COUNT(*)
  FROM reservations r
  WHERE r.vehicle_id = :vehicleId
    AND r.status <> 'CANCELLED'
    AND NOT (r.reservation_end_date <= :start OR r.reservation_start_date >= :end)
    AND (:excludeId IS NULL OR r.id <> :excludeId)
""", nativeQuery = true)
    long countOverlaps(@Param("vehicleId") Long vehicleId,
                       @Param("start") Date start,
                       @Param("end")   Date end,
                       @Param("excludeId") Long excludeId);


    List<Reservation> findAllByUser_Email(String email);
    List<Reservation> findAllByVehicleId(Long id);
    Optional<Reservation> findByStripePaymentId(String stripePaymentId);

}
