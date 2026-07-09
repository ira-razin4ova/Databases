package ru.hogwarts.school.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByBirthDate(LocalDate birthDate);

    List<User> findByBirthDateBetween(LocalDate from, LocalDate to);

    List<User> findByFaculty_Id(Long id); // послу findBy должно быть имя поля в базе по которому мы будем искать, иначе приложение не запускается

    Optional <User> findByEmail (String email);

    Optional<User> findByActivationToken(String token);

    @Query(value = "SELECT COUNT (*) from users", nativeQuery = true)
    Long getUserCount();

    @Query(value = "SELECT * FROM users ORDER BY id desc LIMIT 5", nativeQuery = true)
    List<User> getUserLimitFive();

    @Query("SELECT u FROM User u " +
        "LEFT JOIN FETCH u.faculty " +
        "WHERE u.faculty.id IN :ids " +
        "AND u.status <> 'GRADUATED' " +
        "AND u.status <> 'DISMISSED'")
    List <User> findAllByFacultyId (@Param("ids") Set<Long>ids );

    @Query("SELECT s FROM User s " +
            "LEFT JOIN FETCH s.faculty " +
            //"LEFT JOIN FETCH s.avatar " +
            "WHERE s.studentTicket = :studentTicket")
    List<User> findByStudentTicket(@Param("studentTicket") String studentTicket);

    @Query("SELECT s FROM User s " +
            "LEFT JOIN FETCH s.faculty " +
            //"LEFT JOIN FETCH s.avatar " +
            "WHERE s.lastName = :lastName")
    List<User> findByLastNameIgnoreCase(@Param("lastName") String lastName);

    @Query("SELECT s FROM User s " +
            "LEFT JOIN FETCH s.faculty " +
            //"LEFT JOIN FETCH s.avatar " +
            "WHERE s.phoneNumber = :phone")
    List<User> findByPhoneNumber(@Param("phone") String phone);

    List<User> findAllByFacultyIdIn(Set<Long> facultyId);

}
