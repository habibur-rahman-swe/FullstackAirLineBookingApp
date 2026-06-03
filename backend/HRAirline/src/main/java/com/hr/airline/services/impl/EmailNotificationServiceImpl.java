package com.hr.airline.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.hr.airline.entities.Booking;
import com.hr.airline.entities.User;
import com.hr.airline.ripo.EmailNotificationRepo;
import com.hr.airline.services.EmailNotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

	private final EmailNotificationRepo emailNotificationRepo;
	private final JavaMailSender emailSender;
	private final TemplateEngine templateEngine;
	
	@Value("${app.url.viewBooking}")
	private String viewBookingUrl;
	
	@Value("${app.url.login}")
	private String loginUrl;
	
	@Override
	@Transactional
	@Async
	public void sendBookingTicketEmail(Booking booking) {
		log.info("Inside sendBookingTicketEmail()");
		String recipientEmail = booking.getUser().getEmail();
		String subject = "Your Flight Booking Ticket - Reference";
		String templateName = "booking_ticket";
		
		Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("userName", booking.getUser().getName());
        templateVariables.put("bookingReference", booking.getBookingReference());
        templateVariables.put("flightNumber", booking.getFlight().getFlightNumber());
        templateVariables.put("departureAirportIataCode", booking.getFlight().getDepartureAirport().getIataCode());
        templateVariables.put("departureAirportName", booking.getFlight().getDepartureAirport().getName());
        templateVariables.put("departureAirportCity", booking.getFlight().getDepartureAirport().getCity());
        templateVariables.put("departureTime", booking.getFlight().getDepartureTime());
        templateVariables.put("arrivalAirportIataCode", booking.getFlight().getArrivalAirport().getIataCode());
        templateVariables.put("arrivalAirportName", booking.getFlight().getArrivalAirport().getName());
        templateVariables.put("arrivalAirportCity", booking.getFlight().getArrivalAirport().getCity());
        templateVariables.put("arrivalTime", booking.getFlight().getArrivalTime());
        templateVariables.put("basePrice", booking.getFlight().getBasePrice());
        templateVariables.put("passengers", booking.getPassengers());
        templateVariables.put("viewBookingUrl", viewBookingUrl);
        
        // Render the template context
        Context context = new Context();
        templateVariables.forEach(context::setVariable);
        String emailBody = templateEngine.process(templateName, context);
        
        // send actual email with template
	}
	@Override
	public void sendWelcomeEmail(User user) {
		// TODO Auto-generated method stub
		
	}
	
}
