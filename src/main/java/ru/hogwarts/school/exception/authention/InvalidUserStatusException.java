package ru.hogwarts.school.exception.authention;

import org.springframework.http.HttpStatus;
import ru.hogwarts.school.constant.AuthConstants;

public class InvalidUserStatusException extends AuthException {
    public InvalidUserStatusException(String currentStatus) {
        super(String.format(AuthConstants.Errors.INVALID_USER_STATUS, currentStatus));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
