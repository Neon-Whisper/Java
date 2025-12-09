package com.neon;

import com.neon.ServerAPI;

import java.util.List;

public class HttpClient {
    private ServerAPI serverAPI;

    public HttpClient() {
        this.serverAPI = new ServerAPI();
    }

    public List<Flight> searchFlights(String depCity, String arrCity, String date) {
        return serverAPI.searchFlights(depCity, arrCity, date);
    }

    public boolean createBooking(Booking booking) {
        return serverAPI.createBooking(booking);
    }

    public List<Booking> getAllBookings() {
        return serverAPI.getAllBookings();
    }

    public boolean cancelBooking(String bookingId) {
        return serverAPI.cancelBooking(bookingId);
    }
}

