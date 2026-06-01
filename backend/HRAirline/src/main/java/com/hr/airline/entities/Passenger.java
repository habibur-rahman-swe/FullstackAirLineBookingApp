package com.hr.airline.entities;

import com.hr.airline.enums.PassengerType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "passengers")
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String firstName;
    private String lastName;
    private String passportNumber;

    @Enumerated(EnumType.STRING)
    private PassengerType type;

    private String seatNumber;
    private String specialRequests;
}

