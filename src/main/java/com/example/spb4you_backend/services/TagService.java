package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Tag;
import com.example.spb4you_backend.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService extends GenericService<Tag, Integer> {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        super(tagRepository);
        this.tagRepository = tagRepository;
    }

    // Метод для поиска тега по названию
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }
}
