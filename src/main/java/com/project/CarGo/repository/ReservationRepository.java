package com.project.CarGo.repository;


import com.project.CarGo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r left join fetch r.user")
    List<Reservation> findAllWithUser();

}
