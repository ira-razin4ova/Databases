package ru.hogwarts.school.exception.authention;

import org.springframework.http.HttpStatus;
import ru.hogwarts.school.constant.AuthConstants;

public class UserAlreadyActivatedException extends AuthException {
    public UserAlreadyActivatedException(String email) {
        super(String.format(AuthConstants.Errors.USER_ALREADY_ACTIVATED, email));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
