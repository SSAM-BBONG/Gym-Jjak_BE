package com.ssambbong.gymjjak.tag.application.service;

import com.ssambbong.gymjjak.tag.application.usecase.TagQueryUseCase;
import com.ssambbong.gymjjak.tag.domain.model.Tag;
import com.ssambbong.gymjjak.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagQueryService implements TagQueryUseCase {

    private final TagRepository tagRepository;

    @Override
    public List<TagView> handle() {
        List<Tag> tags = tagRepository.findAll();

        List<Long> tagIds = tags.stream().map(Tag::getId).toList();
        Map<Long, Long> usageMap = tagRepository.countPtCoursesByTagIds(tagIds);

        return tags.stream()
                .map(tag -> new TagView(
                        tag.getId(),
                        tag.getName(),
                        tag.getCreatedAt(),
                        usageMap.getOrDefault(tag.getId(), 0L)
                ))
                .toList();
    }
}
