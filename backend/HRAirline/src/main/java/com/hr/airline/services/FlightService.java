package com.hr.airline.services;

import java.time.LocalDate;
import java.util.List;

import com.hr.airline.dtos.CreateFlightRequest;
import com.hr.airline.dtos.FlightDTO;
import com.hr.airline.dtos.Response;
import com.hr.airline.enums.City;
import com.hr.airline.enums.Country;
import com.hr.airline.enums.FlightStatus;

public interface FlightService {

	Response<?> createFlight(CreateFlightRequest createFlightRequest);

	Response<FlightDTO> getFlightById(Long id);

	Response<List<FlightDTO>> getAllFlights();

	Response<?> updateFlight(CreateFlightRequest createFlightRequest);

	Response<List<FlightDTO>> searchFlight(String departurePortIata, String arrivalPortIata, FlightStatus status,
			LocalDate departureDate);

	Response<List<City>> getAllCities();

	Response<List<Country>> getAllCountries();

}
