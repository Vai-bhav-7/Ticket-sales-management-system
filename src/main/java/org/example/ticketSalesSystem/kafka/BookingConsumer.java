package org.example.ticketSalesSystem.kafka;

import org.example.ticketSalesSystem.dto.BookingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingConsumer {

    @KafkaListener(
            topics = "booking-events",
            groupId = "ticket-sales-group"
    )
    public void consumeBookingEvent(BookingEvent bookingEvent) {

        System.out.println("CONSUMER METHOD TRIGGERED");
        System.out.println("===== BOOKING EVENT RECEIVED =====");

        System.out.println("Booking ID: " + bookingEvent.getBookingId());
        System.out.println("Ticket ID: " + bookingEvent.getTicketId());
        System.out.println("Quantity: " + bookingEvent.getQuantity());
        System.out.println("Total Price: " + bookingEvent.getTotalPrice());
        System.out.println("Username: " + bookingEvent.getUsername());

        System.out.println("==================================");
    }
}