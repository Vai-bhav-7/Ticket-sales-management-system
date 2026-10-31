
package org.example.ticketSalesSystem.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ticketSalesSystem.dto.BookingResponse;
import org.example.ticketSalesSystem.service.BookingService;
import org.example.ticketSalesSystem.service.IdempotencyService;
import org.example.ticketSalesSystem.service.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RateLimitService rateLimitService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public BookingController(
            BookingService bookingService,
            RateLimitService rateLimitService,
            IdempotencyService idempotencyService,
            ObjectMapper objectMapper) {
        this.bookingService = bookingService;
        this.rateLimitService = rateLimitService;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/ticket/{ticketId}")
    public BookingResponse bookTicket(
            @PathVariable Long ticketId,
            @RequestParam int quantity,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            Authentication authentication) {

        String username = authentication.getName();

        if (!rateLimitService.isAllowed(username)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many booking requests. Please try again later.");
        }

        String cachedResponse = idempotencyService.getCachedResponse(idempotencyKey);

        if (cachedResponse != null) {
            if ("PROCESSING".equals(cachedResponse)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This booking request is still processing.");
            }

            try {
                return objectMapper.readValue(cachedResponse, BookingResponse.class);
            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Could not read the cached booking response.");
            }
        }

        if (!idempotencyService.reserveKey(idempotencyKey)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This booking request is already processing.");
        }

        try {
            BookingResponse response =
                    bookingService.bookTicket(ticketId, quantity, username);

            String jsonResponse = objectMapper.writeValueAsString(response);
            idempotencyService.saveResponse(idempotencyKey, jsonResponse);

            return response;

        } catch (Exception e) {
            idempotencyService.removeKey(idempotencyKey);

            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to process booking response.",
                    e
            );
        }
    }

    @PostMapping("/{bookingId}/pay")
    public BookingResponse processPayment(
            @PathVariable Long bookingId,
            @RequestParam boolean success) {

        return bookingService.processPayment(bookingId, success);
    }
}
