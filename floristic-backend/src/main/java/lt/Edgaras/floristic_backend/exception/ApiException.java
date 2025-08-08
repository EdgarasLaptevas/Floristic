package lt.Edgaras.floristic_backend.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    @Autowired
    public ApiException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
