package org.example.ticketSalesSystem.Controller;

import org.example.ticketSalesSystem.Model.Event;
import org.example.ticketSalesSystem.Model.Ticket;
import org.example.ticketSalesSystem.Repository.EventRepository;
import org.example.ticketSalesSystem.Repository.TicketRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketController(TicketRepository ticketRepository,
                            EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/event/{eventId}")
    public Ticket createTicket(@PathVariable Long eventId,
                               @RequestBody Ticket ticket) {

        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            return null;
        }

        ticket.setEvent(event);

        return ticketRepository.save(ticket);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Ticket updateTicket(@PathVariable Long id,
                               @RequestBody Ticket ticket) {

        Ticket existingTicket = ticketRepository.findById(id).orElse(null);

        if (existingTicket == null) {
            return null;
        }

        existingTicket.setTicketType(ticket.getTicketType());
        existingTicket.setPrice(ticket.getPrice());
        existingTicket.setQuantity(ticket.getQuantity());

        return ticketRepository.save(existingTicket);
    }

    @DeleteMapping("/{id}")
    public String deleteTicket(@PathVariable Long id) {

        if (!ticketRepository.existsById(id)) {
            return "Ticket not found";
        }

        ticketRepository.deleteById(id);

        return "Ticket deleted successfully";
    }
}