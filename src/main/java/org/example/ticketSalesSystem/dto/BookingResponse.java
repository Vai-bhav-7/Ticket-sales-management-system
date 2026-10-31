package org.example.ticketSalesSystem.dto;

public class BookingResponse {

    private Long id;
    private int quantity;
    private double totalPrice;
    private String status;
    private String username;
    private Long ticketId;
    private String ticketType;

    public BookingResponse() {
    }

    public BookingResponse(
            Long id,
            int quantity,
            double totalPrice,
            String status,
            String username,
            Long ticketId,
            String ticketType) {

        this.id = id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.username = username;
        this.ticketId = ticketId;
        this.ticketType = ticketType;
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getTicketType() {
        return ticketType;
    }
}