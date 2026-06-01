package com.hr.airline.ripo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.airline.entities.Booking;

public interface BookingRepo extends JpaRepository<Booking, Long> {

	List<Booking> findByUserIdOrderByIdDesc(Long userId);
}
