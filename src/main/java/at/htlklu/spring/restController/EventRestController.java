package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.HateoasUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Event;
import at.htlklu.spring.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("events")
public class EventRestController extends RepresentationModel {
    private static final Logger logger = LogManager.getLogger(EventRestController.class);
    private static final String className = "EventRestController";


    @Autowired
    EventRepository eventRepository;


    //http://localhost:8082/teachers/1
    @GetMapping(value = "{eventId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer eventId) {
        logger.info(LogUtils.info(className, "getByPV", String.format("(%d)", eventId)));
        ResponseEntity<?> result;
        Optional<Event> optionalEvent = eventRepository.findById(eventId);


        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            addLinks(event);
            result = new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Event mit der Id = %d nicht vorhanden", eventId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }


    //region Put and Push
// einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Event event,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "add", String.format("(%s)", event)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                eventRepository.save(event);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(event, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Event event,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "update", String.format("(%s)", event)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                eventRepository.save(event);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(event, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

//endregion

    @DeleteMapping(value = "{eventId}")
    public ResponseEntity<?> deletePV2(@PathVariable Integer eventId) {
        logger.info(LogUtils.info(className, "deletePV2", String.format("(%d)", eventId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Event event = null;


        if (!error) {
            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isPresent()) {
                event = optionalEvent.get();
            } else {
                error = true;
                errorMessage = "Event not found";
            }
        }

        if (!error) {
            try {
                eventRepository.delete(event);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    public static void addLinks(Event event){
        if (HateoasUtils.enableHateoas){
            event.add(WebMvcLinkBuilder.linkTo(methodOn(EventRestController.class)
                            .getByIdPV(event.getEventId()))
                            .withSelfRel());
        }
    }
}




