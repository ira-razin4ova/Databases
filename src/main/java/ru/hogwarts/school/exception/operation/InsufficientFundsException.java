package ru.hogwarts.school.exception.operation;

import org.springframework.http.HttpStatus;
import ru.hogwarts.school.constant.AppConstants;

import java.math.BigDecimal;

public class InsufficientFundsException extends BalanceOperationException {
    private final BigDecimal required;
    private final BigDecimal available;
    public InsufficientFundsException(String message, BigDecimal required, BigDecimal available) {
        super(String.format(AppConstants.Balance.INSUFFICIENT_FUNDS_FORMAT, required, available));
        this.required = required;
        this.available = available;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
