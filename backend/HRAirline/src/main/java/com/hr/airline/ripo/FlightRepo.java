package com.hr.airline.ripo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.airline.entities.Flight;
import com.hr.airline.enums.FlightStatus;

public interface FlightRepo extends JpaRepository<Flight, Long> {
	
	boolean existsByFlightNumber(String flightNumber);

	List<Flight> findByDepartureAirportIataCodeAndArrivalAirportIataCodeAndStatusAndDepartureTimeBetween(
			String departureIataCode, String arrivalIataCode, FlightStatus status, LocalDateTime startOfDay,
			LocalDateTime endOfDay);

}
