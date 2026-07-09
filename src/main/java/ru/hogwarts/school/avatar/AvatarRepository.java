package ru.hogwarts.school.avatar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository <Avatar, Long> {

    Optional <Avatar> findByUserId (Long studentId);
}
