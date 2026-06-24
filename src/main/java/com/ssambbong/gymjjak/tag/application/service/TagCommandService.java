package com.ssambbong.gymjjak.tag.application.service;

import com.ssambbong.gymjjak.tag.application.command.CreateTagCommand;
import com.ssambbong.gymjjak.tag.application.command.DeleteTagCommand;
import com.ssambbong.gymjjak.tag.application.command.UpdateTagCommand;
import com.ssambbong.gymjjak.tag.application.usecase.TagCommandUseCase;
import com.ssambbong.gymjjak.tag.domain.exception.TagAlreadyExistsException;
import com.ssambbong.gymjjak.tag.domain.exception.TagInUseException;
import com.ssambbong.gymjjak.tag.domain.exception.TagNotFoundException;
import com.ssambbong.gymjjak.tag.domain.model.Tag;
import com.ssambbong.gymjjak.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagCommandService implements TagCommandUseCase {

    private final TagRepository tagRepository;

    @Override
    public Long handle(CreateTagCommand command) {
        if (tagRepository.existsByName(command.name())) {
            throw new TagAlreadyExistsException();
        }
        Tag saved = tagRepository.save(Tag.create(command.name()));
        return saved.getId();
    }

    @Override
    public void handle(UpdateTagCommand command) {
        Tag tag = tagRepository.findById(command.id())
                .orElseThrow(TagNotFoundException::new);
        if (!tag.getName().equals(command.name()) && tagRepository.existsByName(command.name())) {
            throw new TagAlreadyExistsException();
        }
        tag.changeName(command.name());
        tagRepository.save(tag);
    }

    @Override
    public void handle(DeleteTagCommand command) {
        tagRepository.findById(command.id())
                .orElseThrow(TagNotFoundException::new);
        int affected = tagRepository.softDeleteIfNotInUse(command.id());
        if (affected == 0) {
            throw new TagInUseException();
        }
    }
}
