package com.project.CarGo.service;
import com.project.CarGo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HoldExpirySweeper {

    private final ReservationRepository repo;
    private final StripeService stripe;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void releaseExpiredHolds() {
        var toCancel = repo.findExpiredHoldIdsAndPiIds();
        for (var row : toCancel) {
            String piId = (String) row[1];
            try { stripe.cancelPaymentIntentSilently(piId); } catch (Exception ignored) {}
        }
        repo.sweepExpireToCancelled();
    }
}