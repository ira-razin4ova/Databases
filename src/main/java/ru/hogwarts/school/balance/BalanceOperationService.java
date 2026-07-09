package ru.hogwarts.school.balance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.balance.dto.*;
import ru.hogwarts.school.balance.dto.batch.*;
import ru.hogwarts.school.balance.enums.Type;
import ru.hogwarts.school.balance.enums.Status;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.faculty.FacultyService;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.task.Task;
import ru.hogwarts.school.user.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BalanceOperationService {

    private final BalanceOperationRepository balanceOperationRepository;
    private final BalanceOperationMapper balanceOperationMapper;
    private final BalanceOperationValidator validator;

    private final UserRepository userRepository;

    private final FacultyService facultyService;

    public BalanceOperation getOperationOrThrow(Long id) {
        return balanceOperationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Операция", id));
    }

    @Transactional
    public BalanceOperationDto createOperationAccrual(CreateBalanceOperationDto dto) {
        User user = validator.expectedUser(dto.userId());
        Faculty faculty = validator.getFaculty(dto.sourceFacultyId(), dto.amount());
        Task task = validator.checkTask(dto.taskId());
        BalanceOperation operationStudent = balanceOperationMapper.toEntity(dto);
        operationStudent.setTask(task);
        operationStudent.setDescription(task.getTitle());
        operationStudent.setSourceFaculty(faculty);
        operationStudent.setUser(user);
        BalanceOperation savedOperation = balanceOperationRepository.save(operationStudent);
        return balanceOperationMapper.toDto(savedOperation); // TODO creator_id берем из jwt token
    }

    @Transactional  // TODO пересмотреть этот метод что бы сумма которая нужна для начисления считалась сразу
    // TODO и в случае нехватки не над было делать откат всех операций
    public BatchOperationResultDto confirmOperations(@Valid BatchConfirmRequestDto request) {

        Map<Long, BatchConfirmRequestDto.PatchItem> updateMap = createUpdateMap(request);

        List<BalanceOperation> operation = balanceOperationRepository.
                findAllByIdInWithRelations(updateMap.keySet().stream().toList());

        validator.checkListValidationConfirm(operation);

        BigDecimal totalDeducted = BigDecimal.ZERO;

        for (BalanceOperation op : operation) {
            BatchConfirmRequestDto.PatchItem item = updateMap.get(op.getId());
            BigDecimal processedAmount = processSingleOperation(op, item);
            totalDeducted = totalDeducted.add(processedAmount);
        }
        return new BatchOperationResultDto(operation.size(), totalDeducted);
    }

    private Map<Long, BatchConfirmRequestDto.PatchItem> createUpdateMap(@Valid BatchConfirmRequestDto request) {
        return request.items().stream()
                .collect(Collectors.toMap(
                        item -> item.operationId(),
                        item -> item
                ));
    }

    public BigDecimal processSingleOperation(BalanceOperation operation, BatchConfirmRequestDto.PatchItem item) {

        BigDecimal finalAmount = (item != null && item.amount() != null) ? item.amount() : operation.getAmount();

        if (item != null && item.taskId() != null) {
            updateTaskOperation(operation, item);
        }

        validator.checkFacultyBalance(operation, finalAmount);
        newStudentBalance(operation, finalAmount);
        newFacultyBalance(operation, finalAmount);
        operation.setAmount(finalAmount);
        operation.setStatus(Status.CONFIRMED);
        return finalAmount;
    }

    public void updateTaskOperation(BalanceOperation operation, BatchConfirmRequestDto.PatchItem item) {
        Task task = validator.checkTask(item.taskId());
        operation.setTask(task);
        operation.setDescription(task.getTitle());
    }

    public void newStudentBalance(BalanceOperation operation, BigDecimal finalAmount) {
        BigDecimal newBalance = operation.getUser().getBalance().add(finalAmount);
        operation.getUser().setBalance(newBalance);
    }

    public void newFacultyBalance(BalanceOperation operation, BigDecimal finalAmount) {
        BigDecimal newBalance = operation.getSourceFaculty().getBalance().subtract(finalAmount);
        operation.getSourceFaculty().setBalance(newBalance);
    }

    public void deleteOperationsDraft(@Valid BatchDeleteRequestDto deleteList) {


        Set<Long> idsToDelete = deleteList.items().stream()
                .map(BatchDeleteRequestDto.DeleteItem::operationId)
                .collect(Collectors.toSet());

        List<BalanceOperation> operations = balanceOperationRepository
                .findAllById(idsToDelete);

        validator.checkListValidationDelete(operations);

        balanceOperationRepository.deleteAllInBatch(operations);
    }

    // TODO валидация что удалить начисления может только тот кто его сделал, creatorId берем из токена
    public BatchOperationResultDto cancelOperations(@Valid BatchCancelRequestDto cancelList) {

        Set<Long> idsToCancel = cancelList.items().stream()
                .map(BatchCancelRequestDto.CancelItem::operationId)
                .collect(Collectors.toSet());

        List<BalanceOperation> operationCancel = balanceOperationRepository.
                findAllByIdInWithRelations(idsToCancel.stream().toList());

        validator.checkListValidationCancel(operationCancel);

        BigDecimal totalCancel = BigDecimal.ZERO;
        for (BalanceOperation op : operationCancel) {
            op.setStatus(Status.CANCELLED);

            User user = op.getUser();
            BigDecimal newBalanceUser = user.getBalance().subtract(op.getAmount());
            user.setBalance(newBalanceUser);

            Faculty faculty = op.getSourceFaculty();
            BigDecimal newBalanceFaculty = faculty.getBalance().add(op.getAmount());
            faculty.setBalance(newBalanceFaculty);

            totalCancel = totalCancel.add(op.getAmount());
        }

        return new BatchOperationResultDto(operationCancel.size(), totalCancel);
    }
    @Transactional
    public List<BalanceOperationDto> createOperationToFaculty(BatchCreateFacultyRequest request) {

        Set<Long> facultyId = new HashSet<>(request.facultyIds());

        List<User> users = userRepository.findAllByFacultyIdIn(facultyId);
        Task task = validator.checkTask(request.taskId());
        Faculty faculty = facultyService.getFacultyOrThrow(request.sourceFacultyId());
        ArrayList<BalanceOperation> operations = new ArrayList<>(users.size());
        for (User u : users) {
            BalanceOperation operation = new BalanceOperation();
            operation.setUser(u);
            operation.setOperationType(Type.ACCRUAL);
            operation.setTask(task);
            operation.setDescription(task.getTitle());
            operation.setAmount(request.amount());
            operation.setSourceFaculty(faculty);
            operation.setStatus(Status.DRAFT);

            operations.add(operation);
        }
        List<BalanceOperation> savedOperations = balanceOperationRepository.saveAll(operations);

        return balanceOperationMapper.toDtoList(savedOperations);
    }

}
