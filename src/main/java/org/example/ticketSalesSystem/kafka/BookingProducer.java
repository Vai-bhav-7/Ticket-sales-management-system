package org.example.ticketSalesSystem.kafka;

import org.example.ticketSalesSystem.dto.BookingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {

    private static final String TOPIC = "booking-events";

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public BookingProducer(KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookingEvent(BookingEvent bookingEvent) {
        kafkaTemplate.send(TOPIC, bookingEvent)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        System.err.println(
                                "Failed to publish booking event: "
                                        + exception.getMessage()
                        );
                    } else {
                        System.out.println(
                                "Booking event published successfully"
                        );
                    }
                });
    }
}