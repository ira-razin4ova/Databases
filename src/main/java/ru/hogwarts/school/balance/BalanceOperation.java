package ru.hogwarts.school.balance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.balance.enums.Type;
import ru.hogwarts.school.balance.enums.Status;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.task.Task;
import ru.hogwarts.school.user.User;

import java.time.OffsetDateTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "balance_operation")
public class BalanceOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "source_faculty_id")
    private Faculty sourceFaculty;

    @Column (name = "operation_type")
    @Enumerated(EnumType.STRING)
    private Type operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceOperation that = (BalanceOperation) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getUser(), that.getUser()) && getOperationType() == that.getOperationType() && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getUser(), getOperationType(), getCreatedAt(), getDescription());
    }

    @Override
    public String toString() {
        return "BalanceOperationStudent{" +
                "amount=" + amount +
                ", id=" + id +
                ", student=" + user +
                ", operationType=" + operationType +
                ", createdAt=" + createdAt +
                ", description='" + description + '\'' +
                '}';
    }
}





