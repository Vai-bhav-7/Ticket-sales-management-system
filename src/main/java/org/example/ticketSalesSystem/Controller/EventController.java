package org.example.ticketSalesSystem.Controller;

import org.example.ticketSalesSystem.Model.Event;
import org.example.ticketSalesSystem.Repository.EventRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Cacheable(value = "events", key = "#id")
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = "events", key = "#id")
    @PutMapping("/{id}")
    public Event updateEvent(
            @PathVariable Long id,
            @RequestBody Event event) {

        Event existingEvent =
                eventRepository.findById(id).orElse(null);

        if (existingEvent == null) {
            return null;
        }

        existingEvent.setName(event.getName());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setTotalTickets(event.getTotalTickets());
        existingEvent.setTicketPrice(event.getTicketPrice());

        return eventRepository.save(existingEvent);
    }

    @CacheEvict(value = "events", key = "#id")
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {

        if (!eventRepository.existsById(id)) {
            return "Event not found";
        }

        eventRepository.deleteById(id);

        return "Event deleted successfully";
    }
}