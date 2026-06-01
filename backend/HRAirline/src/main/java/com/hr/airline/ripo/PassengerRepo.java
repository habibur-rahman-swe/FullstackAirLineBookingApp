package com.hr.airline.ripo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.airline.entities.Passenger;

public interface PassengerRepo extends JpaRepository<Passenger, Long> {
}

