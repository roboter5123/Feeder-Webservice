package com.roboter5123.feeder.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown if a Resource is no longer in the database
 * @author roboter5123
 */
@ResponseStatus(code = HttpStatus.GONE)
public class GoneException extends RuntimeException{

}
