package com.roboter5123.feeder.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when something non descript has gone wrong.
 * @author roboter5123
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends RuntimeException {

}
