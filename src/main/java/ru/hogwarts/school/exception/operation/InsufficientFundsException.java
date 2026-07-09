package ru.hogwarts.school.exception.operation;

import org.springframework.http.HttpStatus;
import ru.hogwarts.school.constant.AppConstants;

import java.math.BigDecimal;
/**
 * Исключение, выбрасываемое при попытке списания средств, превышающих баланс пользователя.
 *
 * <p>Содержит дополнительную информацию для клиента:
 * <ul>
 *   <li>{@code required} — сколько требовалось</li>
 *   <li>{@code available} — сколько было доступно</li>
 * </ul>
 *
 * <p>Эти данные могут быть использованы фронтендом для отображения сообщения
 * вида "Недостаточно средств: нужно 500, у вас 200".
 *
 * <p>Возвращает HTTP-статус {@code 422 UNPROCESSABLE_ENTITY}, так как запрос
 * синтаксически корректен, но семантически не может быть выполнен.
 *
 * @see BalanceOperationException
 */

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
