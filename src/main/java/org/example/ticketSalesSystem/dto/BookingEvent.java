package org.example.ticketSalesSystem.dto;

public class BookingEvent {

    private Long bookingId;
    private Long ticketId;
    private int quantity;
    private double totalPrice;
    private String username;

    public BookingEvent() {
    }

    public BookingEvent(Long bookingId, Long ticketId, int quantity,
                        double totalPrice, String username) {
        this.bookingId = bookingId;
        this.ticketId = ticketId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.username = username;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}