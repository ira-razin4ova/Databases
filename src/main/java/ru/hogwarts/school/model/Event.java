package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @Column(name = "archive")
    private Boolean archive;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();

    @Column (name = "course")
    private Integer targetCourse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId()) &&
                Objects.equals(getTitle(), event.getTitle()) &&
                Objects.equals(getDateStart(), event.getDateStart()) &&
                Objects.equals(getDateEnd(), event.getDateEnd()) &&
                Objects.equals(getArchive(), event.getArchive());
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
