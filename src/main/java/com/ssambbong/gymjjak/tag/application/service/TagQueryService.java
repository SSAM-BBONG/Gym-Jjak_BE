package com.ssambbong.gymjjak.tag.application.service;

import com.ssambbong.gymjjak.tag.application.usecase.TagQueryUseCase;
import com.ssambbong.gymjjak.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagQueryService implements TagQueryUseCase {

    private final TagRepository tagRepository;

    @Override
    public List<TagView> handle() {
        return tagRepository.findAll()
                .stream()
                .map(tag -> new TagView(
                        tag.getId(),
                        tag.getName(),
                        tag.getCreatedAt(),
                        tagRepository.countPtCoursesByTagId(tag.getId())
                ))
                .toList();
    }
}
