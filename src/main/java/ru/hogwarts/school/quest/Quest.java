package ru.hogwarts.school.quest;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ru.hogwarts.school.task.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quest")
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Column(name = "archive", nullable = false)
    private Boolean archive = false;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();

    @Column (name = "course")
    private Short targetCourse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quest quest = (Quest) o;
        return Objects.equals(getId(), quest.getId()) &&
                Objects.equals(getTitle(), quest.getTitle()) &&
                Objects.equals(getDateStart(), quest.getDateStart()) &&
                Objects.equals(getDateEnd(), quest.getDateEnd()) &&
                Objects.equals(getArchive(), quest.getArchive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDateStart(), getDateEnd(), getArchive());
    }

    @Override
    public String toString() {
        return "Event{" +
                "archive=" + archive +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                ", tasks=" + tasks +
                '}';
    }
}
