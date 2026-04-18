package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin-ui/avatars")
@RequiredArgsConstructor
public class AvatarResource {

    private final AvatarRepository avatarRepository;

    private final ObjectMapper objectMapper;

    @GetMapping
    public PagedModel<Avatar> getAll(@ParameterObject Pageable pageable) {
        Page<Avatar> avatars = avatarRepository.findAll(pageable);
        return new PagedModel<>(avatars);
    }

    @GetMapping("/{id}")
    public Avatar getOne(@PathVariable Long id) {
        Optional<Avatar> avatarOptional = avatarRepository.findById(id);
        return avatarOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @GetMapping("/by-ids")
    public List<Avatar> getMany(@RequestParam List<Long> ids) {
        return avatarRepository.findAllById(ids);
    }

    @PostMapping
    public Avatar create(@RequestBody Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    @PatchMapping("/{id}")
    public Avatar patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(avatar).readValue(patchNode);

        return avatarRepository.save(avatar);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        Collection<Avatar> avatars = avatarRepository.findAllById(ids);

        for (Avatar avatar : avatars) {
            objectMapper.readerForUpdating(avatar).readValue(patchNode);
        }

        List<Avatar> resultAvatars = avatarRepository.saveAll(avatars);
        return resultAvatars.stream()
                .map(Avatar::getId)
                .toList();
    }

    @DeleteMapping("/{id}")
    public Avatar delete(@PathVariable Long id) {
        Avatar avatar = avatarRepository.findById(id).orElse(null);
        if (avatar != null) {
            avatarRepository.delete(avatar);
        }
        return avatar;
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        avatarRepository.deleteAllById(ids);
    }
}
