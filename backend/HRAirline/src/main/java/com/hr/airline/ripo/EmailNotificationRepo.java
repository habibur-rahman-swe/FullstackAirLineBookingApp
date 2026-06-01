package com.hr.airline.ripo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr.airline.entities.EmailNotification;

public interface EmailNotificationRepo extends JpaRepository<EmailNotification, Long> {
}

