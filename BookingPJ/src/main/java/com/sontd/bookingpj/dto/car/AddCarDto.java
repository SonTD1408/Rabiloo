package com.sontd.bookingpj.dto.car;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class AddCarDto {
    private Long companyId;
    private int typeId;
    private int emptySpot;
    private String licensePlate;
    private String from;
    private String to;
    private int cost;
    private Timestamp date;
    private List seatBooked;
}
