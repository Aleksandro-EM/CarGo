package com.project.CarGo.repository;

import com.project.CarGo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
                       @Param("end") Date end,
                       @Param("excludeId") Long excludeId);

    List<Reservation> findAllByUser_Email(String email);

    Optional<Reservation> findByStripePaymentId(String stripePaymentId);

    @Query("""
              select r.id, r.stripePaymentId
              from Reservation r
              where r.status = com.project.CarGo.entity.ReservationStatus.PENDING
                and r.holdExpiresAt < current_timestamp
                and r.stripePaymentId is not null
            """)
    List<Object[]> findExpiredHoldIdsAndPiIds();

    @Modifying
    @Transactional
    @Query("""
              update Reservation r
                 set r.status = com.project.CarGo.entity.ReservationStatus.CANCELLED,
                     r.holdExpiresAt = null
               where r.status = com.project.CarGo.entity.ReservationStatus.PENDING
                 and r.holdExpiresAt < current_timestamp
            """)
    int sweepExpireToCancelled();
}