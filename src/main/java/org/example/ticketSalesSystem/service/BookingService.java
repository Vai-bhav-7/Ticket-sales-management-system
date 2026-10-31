
package org.example.ticketSalesSystem.service;

import org.example.ticketSalesSystem.dto.BookingEvent;
import org.example.ticketSalesSystem.dto.BookingResponse;
import org.example.ticketSalesSystem.Exception.BookingException;
import org.example.ticketSalesSystem.Model.Booking;
import org.example.ticketSalesSystem.Model.Ticket;
import org.example.ticketSalesSystem.Model.User;
import org.example.ticketSalesSystem.Repository.BookingRepository;
import org.example.ticketSalesSystem.Repository.TicketRepository;
import org.example.ticketSalesSystem.Repository.UserRepository;
import org.example.ticketSalesSystem.kafka.BookingProducer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final BookingProducer bookingProducer;

    public BookingService(
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            BookingProducer bookingProducer) {

        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.bookingProducer = bookingProducer;
    }

    @Transactional
    public BookingResponse bookTicket(
            Long ticketId,
            int quantity,
            String username) {

        if (quantity <= 0) {
            throw new BookingException(
                    "Quantity must be greater than 0"
            );
        }

        Ticket ticket = ticketRepository
                .findByIdWithLock(ticketId)
                .orElseThrow(() ->
                        new BookingException("Ticket not found"));

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new BookingException("User not found"));

        if (ticket.getQuantity() < quantity) {
            throw new BookingException(
                    "Not enough tickets available"
            );
        }

        // Reserve tickets while holding the database row lock.
        ticket.setQuantity(ticket.getQuantity() - quantity);

        Booking booking = new Booking();
        booking.setQuantity(quantity);
        booking.setTotalPrice(ticket.getPrice() * quantity);
        booking.setStatus("PENDING");
        booking.setUser(user);
        booking.setTicket(ticket);

        Booking savedBooking = bookingRepository.save(booking);

        // Publish the booking event to Kafka.
        BookingEvent bookingEvent = new BookingEvent(
                savedBooking.getId(),
                ticket.getId(),
                savedBooking.getQuantity(),
                savedBooking.getTotalPrice(),
                user.getUsername()
        );

        bookingProducer.sendBookingEvent(bookingEvent);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getQuantity(),
                savedBooking.getTotalPrice(),
                savedBooking.getStatus(),
                user.getUsername(),
                ticket.getId(),
                ticket.getTicketType()
        );
    }

    @Transactional
    public BookingResponse processPayment(
            Long bookingId,
            boolean paymentSuccessful) {

        // Lock the booking to prevent concurrent payment processing.
        Booking booking = bookingRepository
                .findByIdWithLock(bookingId)
                .orElseThrow(() ->
                        new BookingException("Booking not found"));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new BookingException(
                    "Payment cannot be processed for booking with status: "
                            + booking.getStatus()
            );
        }

        if (paymentSuccessful) {
            booking.setStatus("CONFIRMED");
        } else {
            // Lock the ticket before returning reserved inventory.
            Ticket ticket = ticketRepository
                    .findByIdWithLock(booking.getTicket().getId())
                    .orElseThrow(() ->
                            new BookingException("Ticket not found"));

            ticket.setQuantity(
                    ticket.getQuantity() + booking.getQuantity()
            );

            booking.setStatus("FAILED");
        }

        Booking updatedBooking = bookingRepository.save(booking);

        return new BookingResponse(
                updatedBooking.getId(),
                updatedBooking.getQuantity(),
                updatedBooking.getTotalPrice(),
                updatedBooking.getStatus(),
                updatedBooking.getUser().getUsername(),
                updatedBooking.getTicket().getId(),
                updatedBooking.getTicket().getTicketType()
        );
    }
}
