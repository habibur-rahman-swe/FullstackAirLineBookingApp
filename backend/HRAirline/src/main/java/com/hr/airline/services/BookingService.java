package com.hr.airline.services;

import java.util.List;

import com.hr.airline.dtos.BookingDTO;
import com.hr.airline.dtos.CreateBookingRequest;
import com.hr.airline.dtos.Response;
import com.hr.airline.enums.BookingStatus;

public interface BookingService {

	Response<?> createBooking(CreateBookingRequest createBookingRequest);

	Response<BookingDTO> getBookingById(Long id);

	Response<List<BookingDTO>> getAllBookings();

	Response<List<BookingDTO>> getMyBookings();

	Response<?> updateBookingStatus(Long id, BookingStatus status);
}
