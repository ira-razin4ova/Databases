package ru.hogwarts.school.model;

public enum StudentStatus {

    ACTIVE("Активен"),
    ACADEMIC_LEAVE("В академическом отпуске"),
    DISMISSED("Отчислен"),
    GRADUATED("Выпустился"),
    NOT_FILED("Еще не зачислен");

    private final String status;

    StudentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
