package com.roboter5123.feeder.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown if the feeder isn't connected but the request could otherwise be fully processed
 * @author roboter5123
 */
@ResponseStatus(code = HttpStatus.ACCEPTED)
public class AcceptedException extends RuntimeException {

}
