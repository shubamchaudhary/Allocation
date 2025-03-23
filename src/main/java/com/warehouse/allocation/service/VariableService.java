package com.warehouse.allocation.service;

import com.warehouse.allocation.dto.NeedVariableDTO;
import com.warehouse.allocation.dto.PriorityVariableDTO;
import com.warehouse.allocation.exception.ResourceNotFoundException;
import com.warehouse.allocation.model.NeedVariable;
import com.warehouse.allocation.model.PriorityVariable;
import com.warehouse.allocation.repository.NeedVariableRepository;
import com.warehouse.allocation.repository.PriorityVariableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariableService {

    private final NeedVariableRepository needVariableRepository;
    private final PriorityVariableRepository priorityVariableRepository;

    // Need Variable Methods
    @Transactional(readOnly = true)
    public List<NeedVariableDTO> getAllNeedVariables() {
        return needVariableRepository.findAll().stream()
                .map(this::mapToNeedVariableDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NeedVariableDTO getNeedVariableById(UUID id) {
        return needVariableRepository.findById(id)
                .map(this::mapToNeedVariableDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Need variable not found with id: " + id));
    }

    @Transactional
    public NeedVariableDTO createNeedVariable(NeedVariableDTO needVariableDTO) {
        NeedVariable needVariable = mapToNeedVariableEntity(needVariableDTO);
        NeedVariable savedVariable = needVariableRepository.save(needVariable);
        return mapToNeedVariableDTO(savedVariable);
    }

    @Transactional
    public NeedVariableDTO updateNeedVariable(UUID id, NeedVariableDTO needVariableDTO) {
        NeedVariable existingVariable = needVariableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Need variable not found with id: " + id));

        existingVariable.setName(needVariableDTO.getName());
        existingVariable.setDescription(needVariableDTO.getDescription());

        NeedVariable updatedVariable = needVariableRepository.save(existingVariable);
        return mapToNeedVariableDTO(updatedVariable);
    }

    @Transactional
    public void deleteNeedVariable(UUID id) {
        if (!needVariableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Need variable not found with id: " + id);
        }
        needVariableRepository.deleteById(id);
    }

    // Priority Variable Methods
    @Transactional(readOnly = true)
    public List<PriorityVariableDTO> getAllPriorityVariables() {
        return priorityVariableRepository.findAll().stream()
                .map(this::mapToPriorityVariableDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PriorityVariableDTO getPriorityVariableById(UUID id) {
        return priorityVariableRepository.findById(id)
                .map(this::mapToPriorityVariableDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Priority variable not found with id: " + id));
    }

    @Transactional
    public PriorityVariableDTO createPriorityVariable(PriorityVariableDTO priorityVariableDTO) {
        PriorityVariable priorityVariable = mapToPriorityVariableEntity(priorityVariableDTO);
        PriorityVariable savedVariable = priorityVariableRepository.save(priorityVariable);
        return mapToPriorityVariableDTO(savedVariable);
    }

    @Transactional
    public PriorityVariableDTO updatePriorityVariable(UUID id, PriorityVariableDTO priorityVariableDTO) {
        PriorityVariable existingVariable = priorityVariableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Priority variable not found with id: " + id));

        existingVariable.setName(priorityVariableDTO.getName());
        existingVariable.setDescription(priorityVariableDTO.getDescription());

        PriorityVariable updatedVariable = priorityVariableRepository.save(existingVariable);
        return mapToPriorityVariableDTO(updatedVariable);
    }

    @Transactional
    public void deletePriorityVariable(UUID id) {
        if (!priorityVariableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Priority variable not found with id: " + id);
        }
        priorityVariableRepository.deleteById(id);
    }

    // Mapping methods
    private NeedVariableDTO mapToNeedVariableDTO(NeedVariable needVariable) {
        return NeedVariableDTO.builder()
                .id(needVariable.getId())
                .name(needVariable.getName())
                .description(needVariable.getDescription())
                .createdAt(needVariable.getCreatedAt())
                .updatedAt(needVariable.getUpdatedAt())
                .build();
    }

    private NeedVariable mapToNeedVariableEntity(NeedVariableDTO needVariableDTO) {
        return NeedVariable.builder()
                .id(needVariableDTO.getId())
                .name(needVariableDTO.getName())
                .description(needVariableDTO.getDescription())
                .build();
    }

    private PriorityVariableDTO mapToPriorityVariableDTO(PriorityVariable priorityVariable) {
        return PriorityVariableDTO.builder()
                .id(priorityVariable.getId())
                .name(priorityVariable.getName())
                .description(priorityVariable.getDescription())
                .createdAt(priorityVariable.getCreatedAt())
                .updatedAt(priorityVariable.getUpdatedAt())
                .build();
    }

    private PriorityVariable mapToPriorityVariableEntity(PriorityVariableDTO priorityVariableDTO) {
        return PriorityVariable.builder()
                .id(priorityVariableDTO.getId())
                .name(priorityVariableDTO.getName())
                .description(priorityVariableDTO.getDescription())
                .build();
    }
}