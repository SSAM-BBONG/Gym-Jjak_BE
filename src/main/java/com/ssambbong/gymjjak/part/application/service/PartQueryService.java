package com.ssambbong.gymjjak.part.application.service;

import com.ssambbong.gymjjak.part.application.usecase.PartQueryUseCase;
import com.ssambbong.gymjjak.part.domain.model.Part;
import com.ssambbong.gymjjak.part.domain.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartQueryService implements PartQueryUseCase {

    private final PartRepository partRepository;

    @Override
    public List<PartView> handle() {
        List<Part> parts = partRepository.findAll();

        List<Long> partIds = parts.stream().map(Part::getId).toList();
        Map<Long, Long> usageMap = partRepository.countPtCoursesByPartIds(partIds);

        return parts.stream()
                .map(part -> new PartView(
                        part.getId(),
                        part.getName(),
                        part.getCreatedAt(),
                        usageMap.getOrDefault(part.getId(), 0L)
                ))
                .toList();
    }
}
