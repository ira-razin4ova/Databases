package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "title")
    private String title;

    @Column (name = "award")
    private Integer award;

    @ManyToOne
    @JoinColumn (name = "quest_id")
    @JsonBackReference
    private Quest quest;

    @Column (name = "archive")
    private Boolean archive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return  Objects.equals(getId(), task.getId()) &&
                Objects.equals(getTitle(), task.getTitle()) &&
                Objects.equals(getAward(), task.getAward()) &&
                Objects.equals(getArchive(), task.getArchive());
    }

    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), getId(), getTitle(), getAward(), getQuest(), getArchive());
    }

    @Override
    public String toString() {
        return "Task{" +
                "archive=" + archive +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", award=" + award +
                ", event=" + quest.getId() +
                '}';
    }
}
