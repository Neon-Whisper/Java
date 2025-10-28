package com.neon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerAPI {
    private List<Airline> airlines;
    private List<Flight> flights;
    private List<Booking> bookings;

    public ServerAPI() {
        initializeData();
    }

    private void initializeData() {
        airlines = new ArrayList<>();
        airlines.add(new Airline("CA", "中国国航", 0.05));
        airlines.add(new Airline("MU", "东方航空", 0.03));

        flights = new ArrayList<>();
        flights.add(new Flight("CA1301", "北京", "上海", "08:00", "10:20",
                "2025-10-28", 1200, airlines.get(0)));
        flights.add(new Flight("MU5102", "北京", "上海", "09:30", "11:50",
                "2025-10-28", 1100, airlines.get(1)));
        flights.add(new Flight("CA1503", "北京", "上海", "14:00", "16:15",
                "2025-10-28", 1050, airlines.get(0)));

        bookings = new ArrayList<>();
    }

    public List<Flight> searchFlights(String depCity, String arrCity, String date) {
        return flights.stream()
                .filter(f -> f.getDepCity().equals(depCity) &&
                        f.getArrCity().equals(arrCity) &&
                        f.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public boolean createBooking(Booking booking) {
        bookings.add(booking);
        return true;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public boolean cancelBooking(String bookingId) {
        return bookings.removeIf(booking -> {
            if (booking.getBookingId().equals(bookingId)) {
                // 释放座位
                for (Seat seat : booking.getSeats()) {
                    seat.setEmpty(true);
                }
                return true;
            }
            return false;
        });
    }
}

