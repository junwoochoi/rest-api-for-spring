package me.junu.restapiforspring.Events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ModelMapper modelMapper;


    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto) {

        Event event = modelMapper.map(eventDto, Event.class);

        Event newEvent = eventRepository.save(event);


        URI createdUri = linkTo((EventController.class))
                .slash(newEvent.getId())
                .toUri();

        return ResponseEntity.created(createdUri).body(event);
    }

}
