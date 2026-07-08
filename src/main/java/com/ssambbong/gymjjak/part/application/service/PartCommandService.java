package com.ssambbong.gymjjak.part.application.service;

import com.ssambbong.gymjjak.part.application.command.CreatePartCommand;
import com.ssambbong.gymjjak.part.application.command.DeletePartCommand;
import com.ssambbong.gymjjak.part.application.command.UpdatePartCommand;
import com.ssambbong.gymjjak.part.application.usecase.PartCommandUseCase;
import com.ssambbong.gymjjak.part.domain.exception.PartAlreadyExistsException;
import com.ssambbong.gymjjak.part.domain.exception.PartInUseException;
import com.ssambbong.gymjjak.part.domain.exception.PartNotFoundException;
import com.ssambbong.gymjjak.part.domain.model.Part;
import com.ssambbong.gymjjak.part.domain.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartCommandService implements PartCommandUseCase {

    private final PartRepository partRepository;

    @Override
    public Long handle(CreatePartCommand command) {
        if (partRepository.existsByName(command.name())) {
            throw new PartAlreadyExistsException();
        }
        Part saved = partRepository.save(Part.create(command.name()));
        return saved.getId();
    }

    @Override
    public void handle(UpdatePartCommand command) {
        Part part = partRepository.findById(command.id())
                .orElseThrow(PartNotFoundException::new);
        if (!part.getName().equals(command.name()) && partRepository.existsByName(command.name())) {
            throw new PartAlreadyExistsException();
        }
        part.changeName(command.name());
        partRepository.save(part);
    }

    @Override
    public void handle(DeletePartCommand command) {
        partRepository.findById(command.id())
                .orElseThrow(PartNotFoundException::new);
        if (partRepository.countPtCoursesByPartId(command.id()) > 0) {
            throw new PartInUseException();
        }
        try {
            partRepository.deleteById(command.id());
        } catch (DataIntegrityViolationException e) {
            throw new PartInUseException();
        }
    }
}
