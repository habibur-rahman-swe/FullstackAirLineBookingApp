package com.hr.airline.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.hr.airline.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private Long id;

    private String bookingReference;

    private UserDTO user;

    private FlightDTO flight;

    private LocalDateTime bookingDate;

    private BookingStatus status;

    private List<PassengerDTO> passengers;
}