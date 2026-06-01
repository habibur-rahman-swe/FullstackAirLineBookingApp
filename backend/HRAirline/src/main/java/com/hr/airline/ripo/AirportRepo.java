package com.hr.airline.ripo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.airline.entities.Airport;

public interface AirportRepo extends JpaRepository<Airport, Long> {
	Optional<Airport> findByIataCode(String iataCode);
}
