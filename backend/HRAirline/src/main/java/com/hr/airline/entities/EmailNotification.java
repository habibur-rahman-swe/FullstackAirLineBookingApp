package com.hr.airline.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "email_notifications")
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @NotBlank(message = "recipient is required")
    private String recipientEmail;

    @Lob
    private String body;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private LocalDateTime sentAt;

    private boolean isHtml;
}
