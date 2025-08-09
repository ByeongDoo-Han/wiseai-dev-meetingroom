package com.example.meetingroom.util;

import com.example.meetingroom.aop.ValidReservationTimeRange;
import com.example.meetingroom.dto.reservation.ReservationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TimeRangeValidator implements ConstraintValidator<ValidReservationTimeRange, ReservationRequest> {

    public boolean isValid(ReservationRequest request, ConstraintValidatorContext context){
        LocalDateTime starTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        if(starTime==null || endTime ==null){
            return true;
        }

        boolean isValid = endTime.isAfter(starTime);

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("endTime")
                .addConstraintViolation();
        }

        return isValid;
    }
}
