package me.junu.restapiforspring.Events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors){
        validatePrice(eventDto, errors);
        validateEventDateTime(eventDto, errors, eventDto.getEndEventDateTime());
        validateBeginEventDateTime(eventDto, errors, eventDto.getBeginEventDateTime());
        validateCloseEnrollmentDateTime(eventDto, errors, eventDto.getCloseEnrollmentDateTime());
    }

    private void validateCloseEnrollmentDateTime(EventDto eventDto, Errors errors, LocalDateTime closeEnrollmentDateTime) {
        if(closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong");
        }
    }

    private void validateBeginEventDateTime(EventDto eventDto, Errors errors, LocalDateTime beginEventDateTime) {
        if(beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
        beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        beginEventDateTime.isAfter(eventDto.getEndEventDateTime())){
            errors.rejectValue("beginEventDateTime", "wrongValue","beginEventDateTime is wrong");
        }
    }

    private void validateEventDateTime(EventDto eventDto, Errors errors, LocalDateTime endEventDateTime) {
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }
    }

    private void validatePrice(EventDto eventDto, Errors errors) {
        if(eventDto.getBasePrice()>eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
        }
    }
}
