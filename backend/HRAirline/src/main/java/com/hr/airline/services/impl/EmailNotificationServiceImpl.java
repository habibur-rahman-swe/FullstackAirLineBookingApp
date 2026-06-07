package com.hr.airline.services.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.hr.airline.entities.Booking;
import com.hr.airline.entities.EmailNotification;
import com.hr.airline.entities.User;
import com.hr.airline.ripo.EmailNotificationRepo;
import com.hr.airline.services.EmailNotificationService;

import jakarta.mail.internet.MimeMessage;
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
		sendMailOut(recipientEmail, subject, emailBody, true, booking);
	}

	@Override
	@Transactional
	@Async
	public void sendWelcomeEmail(User user) {
		log.info("Sending welcome email to user: {}", user.getEmail());

		String recipientEmail = user.getEmail();
		String subject = "Welcome to Phegon Airline!";
		String templateName = "welcome_user"; // Hardcoded template name for internal use

		Map<String, Object> templateVariables = new HashMap<>();
		templateVariables.put("userName", user.getName());
		templateVariables.put("frontendLoginUrl", loginUrl);

		// Render the template content
		Context context = new Context();
		templateVariables.forEach(context::setVariable);
		String emailBody = templateEngine.process(templateName, context);

		sendMailOut(recipientEmail, subject, emailBody, true, null);
	}

	private void sendMailOut(String recipientEmail, String subject, String body, boolean isHtml, Booking booking) {
		try {

			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
					MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
			helper.setTo(recipientEmail);
			helper.setSubject(subject);
			helper.setText(body, isHtml);

			log.info("About to send Email...");
			emailSender.send(mimeMessage);

			log.info("Email sent out ");

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// save to the notification database table
		EmailNotification emailNotification = new EmailNotification();
		emailNotification.setRecipientEmail(recipientEmail);
		emailNotification.setSubject(subject);
		emailNotification.setBody(body);
		emailNotification.setHtml(isHtml);
		emailNotification.setSentAt(LocalDateTime.now());
		emailNotification.setBooking(booking);

		emailNotificationRepo.save(emailNotification);
	}

}
