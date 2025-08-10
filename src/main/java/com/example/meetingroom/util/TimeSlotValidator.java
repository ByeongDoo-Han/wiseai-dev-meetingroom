package com.example.meetingroom.util;

import com.example.meetingroom.aop.ValidReservationTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TimeSlotValidator implements ConstraintValidator<ValidReservationTime, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime time, ConstraintValidatorContext context) {
        if (time == null) {
            return true;
        }

        int minute = time.getMinute();
        return minute == 0 || minute == 30;
    }
}
