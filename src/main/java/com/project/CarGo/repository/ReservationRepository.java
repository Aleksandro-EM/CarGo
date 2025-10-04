package com.project.CarGo.repository;


import com.project.CarGo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r left join fetch r.user")
    List<Reservation> findAllWithUser();

    @Query("""
select count(r) from Reservation r
where r.vehicleId = :vehicleId
  and r.status <> com.project.CarGo.entity.ReservationStatus.CANCELLED
  and r.reservationStartDate < :end
  and r.reservationEndDate > :start
  and (:excludeId is null or r.id <> :excludeId)
""")
    long countOverlaps(Long vehicleId, Date start, Date end, Long excludeId);

    List<Reservation> findAllByUser_Email(String email);
    List<Reservation> findAllByVehicleId(Long id);
    Optional<Reservation> findByStripePaymentId(String stripePaymentId);

}
