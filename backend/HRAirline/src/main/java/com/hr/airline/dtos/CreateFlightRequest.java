package com.hr.airline.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hr.airline.enums.FlightStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFlightRequest {

    private Long id;

    private FlightStatus status;

    @NotBlank(message = "Flight number cannot be blank")
    private String flightNumber;

    @NotBlank(message = "Departure airport IATA code cannot be blank")
    private String departureAirportIataCode;

    @NotBlank(message = "Arrival airport IATA code cannot be blank")
    private String arrivalAirportIataCode;

    @NotNull(message = "Departure time cannot be null")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time cannot be null")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Base price cannot be null")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private Long pilotId;
}

