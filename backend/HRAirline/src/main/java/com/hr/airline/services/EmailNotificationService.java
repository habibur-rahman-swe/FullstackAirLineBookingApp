package com.hr.airline.services;

import com.hr.airline.entities.Booking;
import com.hr.airline.entities.User;

public interface EmailNotificationService {
	void sendBookingTicketEmail(Booking booking);

	void sendWelcomeEmail(User user);
}
