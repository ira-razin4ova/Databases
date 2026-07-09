package ru.hogwarts.school.balance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.constant.AppConstants;
import ru.hogwarts.school.balance.enums.Type;
import ru.hogwarts.school.balance.enums.Status;
import ru.hogwarts.school.exception.badrequest.ValidationException;
import ru.hogwarts.school.exception.operation.InsufficientFundsException;
import ru.hogwarts.school.exception.operation.OperationConflictException;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.faculty.FacultyService;
import ru.hogwarts.school.task.Task;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.task.TaskService;
import ru.hogwarts.school.user.UserService;

import java.math.BigDecimal;
import java.util.List;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BalanceOperationValidator {
    private final UserService userService;
    private final FacultyService facultyService;
    private final TaskService taskService;


    public User expectedUser(Long id) {
        User user = userService.getUserOrThrow(id);
        if (user.getStatus().equals(ru.hogwarts.school.user.enums.Status.DISMISSED)) {
            throw new ValidationException("Студен отчислен");
        }
        return user;
    }

    public Faculty getFaculty(Long id, BigDecimal amount) {
        Faculty faculty = facultyService.getFacultyOrThrow(id);
        if (faculty.getBalance() == null || faculty.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(AppConstants.Balance.INSUFFICIENT_FUNDS_FORMAT, amount, faculty.getBalance());
        }
        return faculty;
    }

    public Task checkTask(Long id) {
        return taskService.getTaskOrThrow(id);
    }

    public void checkListValidationConfirm(List<BalanceOperation> listConfirm) {
        for (BalanceOperation op : listConfirm) {
            checkBeforeConfirm(op);
        }
    }

    public void checkBeforeConfirm(BalanceOperation operation) {
        if (!operation.getStatus().equals(Status.DRAFT)) {
            throw new OperationConflictException(
                    String.format(AppConstants.Balance.CONFIRM_CONFLICT_FORMAT, operation.getStatus())
            );
        }
        if (operation.getOperationType().equals(Type.SPEND)) {
            throw new OperationConflictException(
                    String.format(AppConstants.Balance.TYPE_CONFLICT_FORMAT, operation.getOperationType())
            );
        }
    }

    public void checkFacultyBalance(BalanceOperation operation, BigDecimal finalAmount) {
        if (operation.getSourceFaculty().getBalance().compareTo(finalAmount) < 0) {
            throw new InsufficientFundsException(AppConstants.Balance.INSUFFICIENT_FUNDS_FORMAT, finalAmount, operation.getSourceFaculty().getBalance());
        }
    }

    public void checkListValidationDelete(List<BalanceOperation> listDelete) {
        for (BalanceOperation op : listDelete) {
            checkBeforeDelete(op);
        }
    }

    //TODO валидация кто начислил
    public void checkBeforeDelete(BalanceOperation operation) {
        if (!operation.getStatus().equals(Status.DRAFT)) {
            throw new OperationConflictException(
                    String.format(AppConstants.Balance.DELETE_CONFLICT_FORMAT, operation.getStatus()));
        }
        if (operation.getOperationType().equals(Type.SPEND)) {
            throw new OperationConflictException(
                    String.format(AppConstants.Balance.TYPE_CONFLICT_FORMAT, operation.getOperationType()));
        }
    }

    public void checkListValidationCancel(List<BalanceOperation> listCancel) {
        for (BalanceOperation op : listCancel) {
            checkBeforeCancel(op);
        }
    }

    //TODO валидация кто начислил
    public void checkBeforeCancel(BalanceOperation operation) {
        if (!operation.getStatus().equals(Status.CONFIRMED)) {
            throw new OperationConflictException(
                    String.format(AppConstants.Balance.CANCEL_CONFLICT_FORMAT, operation.getStatus()));
        }
        if (operation.getOperationType().equals(Type.SPEND)) {
            throw new OperationConflictException(
                    String.format(AppConstants.Balance.TYPE_CONFLICT_FORMAT, operation.getOperationType()));
        }
        User user = userService.getUserOrThrow(operation.getUser().getId());
        if (user.getBalance() == null || user.getBalance().compareTo(operation.getAmount()) < 0) {
            throw new InsufficientFundsException(AppConstants.Balance.INSUFFICIENT_FUNDS_FORMAT, operation.getAmount(), user.getBalance());
        }
    }

}
