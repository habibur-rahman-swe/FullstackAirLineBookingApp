package com.hr.airline.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hr.airline.dtos.BookingDTO;
import com.hr.airline.dtos.CreateBookingRequest;
import com.hr.airline.dtos.Response;
import com.hr.airline.entities.Booking;
import com.hr.airline.entities.Flight;
import com.hr.airline.entities.Passenger;
import com.hr.airline.entities.User;
import com.hr.airline.enums.BookingStatus;
import com.hr.airline.enums.FlightStatus;
import com.hr.airline.exceptions.BadRequestException;
import com.hr.airline.exceptions.NotFoundException;
import com.hr.airline.ripo.BookingRepo;
import com.hr.airline.ripo.FlightRepo;
import com.hr.airline.ripo.PassengerRepo;
import com.hr.airline.services.BookingService;
import com.hr.airline.services.EmailNotificationService;
import com.hr.airline.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final BookingRepo bookingRepo;
	private final UserService userService;
	private final FlightRepo flightRepo;
	private final PassengerRepo passengerRepo;
	private final ModelMapper modelMapper;
	private final EmailNotificationService emailNotificationService;

	@Override
    @Transactional
    public Response<?> createBooking(CreateBookingRequest createBookingRequest) {

        User user = userService.currentUser();

        Flight flight = flightRepo.findById(createBookingRequest.getFlightId())
                .orElseThrow(()-> new NotFoundException("Flight Not Found"));

        if (flight.getStatus() != FlightStatus.SCHEDULED){
            throw new BadRequestException("You can only book a flight that is scheduled");
        }

        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepo.save(booking);

        if (createBookingRequest.getPassengers() != null && !createBookingRequest.getPassengers().isEmpty()){

            List<Passenger> passengers = createBookingRequest.getPassengers().stream()
                    .map(passengerDTO -> {
                        Passenger passenger = modelMapper.map(passengerDTO, Passenger.class);
                        passenger.setBooking(savedBooking);
                        return passenger;
                    }).toList();

            passengerRepo.saveAll(passengers);
            savedBooking.setPassengers(passengers);
        }

        //SEND EMAIL TICKER OUT
        emailNotificationService.sendBookingTicketEmail(savedBooking);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking created successfully")
                .build();

    }

    @Override
    public Response<BookingDTO> getBookingById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(()-> new NotFoundException("Booking not found"));

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.getFlight().setBookings(null);

        return Response.<BookingDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking retreived successfully")
                .data(bookingDTO)
                .build();
    }

    @Override
    public Response<List<BookingDTO>> getAllBookings() {

        List<Booking> allBookings = bookingRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<BookingDTO> bookings = allBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.getFlight().setBookings(null);
                    return bookingDTO;
                }).toList();

        return Response.<List<BookingDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(bookings.isEmpty()? "No Booking Found" : "Booking retreived successfully")
                .data(bookings)
                .build();
    }

    @Override
    public Response<List<BookingDTO>> getMyBookings() {
        User user = userService.currentUser();
        List<Booking> userBookings = bookingRepo.findByUserIdOrderByIdDesc(user.getId());


        List<BookingDTO> bookings = userBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.getFlight().setBookings(null);
                    return bookingDTO;
                }).toList();

        return Response.<List<BookingDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(bookings.isEmpty()? "No Booking Found for this user" : "User Bookings retrieved successfully")
                .data(bookings)
                .build();

    }

    @Override
    @Transactional
    public Response<?> updateBookingStatus(Long id, BookingStatus status) {

        Booking booking = bookingRepo.findById(id)
                .orElseThrow(()-> new NotFoundException("Booking Not Found"));

        booking.setStatus(status);
        bookingRepo.save(booking);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking Updated Successfully")
                .build();

    }

    private String generateBookingReference(){
        return UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
	
}
