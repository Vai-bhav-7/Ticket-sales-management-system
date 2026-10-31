package org.example.ticketSalesSystem.Repository;

import org.example.ticketSalesSystem.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}