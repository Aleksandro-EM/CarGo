package com.project.CarGo.controller;

import com.project.CarGo.entity.ReservationStatus;
import com.project.CarGo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationRepository reservationRepository;

    @GetMapping(value = "/api/reservations/{id}/hold", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> checkHold(@PathVariable Long id) {
        var r = reservationRepository.findById(id).orElse(null);
        if (r == null) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of("valid", false, "status", "NOT_FOUND", "holdExpiresAt", null));
        }
        boolean valid = r.getStatus() == ReservationStatus.PENDING
                && r.getHoldExpiresAt() != null
                && r.getHoldExpiresAt().toInstant().isAfter(Instant.now());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(Map.of(
                        "valid", valid,
                        "status", r.getStatus() == null ? null : r.getStatus().name(),
                        "holdExpiresAt", r.getHoldExpiresAt() == null ? null : r.getHoldExpiresAt().toInstant().toString()
                ));
    }
}
