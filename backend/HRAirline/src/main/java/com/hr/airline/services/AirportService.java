package com.hr.airline.services;

import java.util.List;

import com.hr.airline.dtos.AirportDTO;
import com.hr.airline.dtos.Response;

public interface AirportService {

	Response<?> createAirport(AirportDTO airportDTO);

	Response<?> updateAirport(AirportDTO airportDTO);

	Response<List<AirportDTO>> getAllAirports();

	Response<AirportDTO> getAirportById(Long id);

}
