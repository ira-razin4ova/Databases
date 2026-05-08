package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import ru.hogwarts.school.dto.quest.*;
import ru.hogwarts.school.service.QuestService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/quest")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;


    @GetMapping("/{id}")
    public ResponseEntity <QuestDto> getEventById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(questService.getByIdQuest(id));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<QuestFullDto> getByIdFullQuest(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(questService.geByIdQuestFull(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<QuestDto>> allQuestWithTaskId() {
        return ResponseEntity.ok(questService.findAllWithTasks());
    }

    @GetMapping("/short-all")
    public ResponseEntity<List <QuestShortDto>> getAllShortQuest() {
        return ResponseEntity.ok(questService.getByAllQuestShort());
    }

    @GetMapping("active/{studentId}")
    public ResponseEntity <List <QuestDto>> getQuestActive (@PathVariable @Positive Long studentId) {
        return ResponseEntity.ok(questService.getByQuestActive(studentId));
    }

    @PostMapping
    public ResponseEntity<QuestFullDto> createQuest(@RequestBody @Valid CreateQuestDto createQuestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(questService.createEvent(createQuestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuestFullDto> updateQuestAndTasksAndCreateTask(@PathVariable @Positive Long id,
                                                                        @RequestBody @Valid PatchQuestDto patchQuestDto) {
        return ResponseEntity.ok(questService.patchEvent(id, patchQuestDto));
    }

    @GetMapping ("/check/{id}")
    public ResponseEntity <CheckTaskFotThisQuestDto> checkTaskFotThisQuest(@PathVariable @Positive Long id){
        return ResponseEntity.ok(questService.checkBeforeDelete(id));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity <String> deleteQuestAndTasksThisQuest (@PathVariable @Positive Long id) {
        questService.deleteEvent(id);
        return ResponseEntity.ok("Event" +id + "and this tasks successfully removed");
    }
}
