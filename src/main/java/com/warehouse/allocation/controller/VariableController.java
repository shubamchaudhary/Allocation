package com.warehouse.allocation.controller;

import com.warehouse.allocation.dto.NeedVariableDTO;
import com.warehouse.allocation.dto.PriorityVariableDTO;
import com.warehouse.allocation.service.VariableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VariableController {

    private final VariableService variableService;

    // Need Variable endpoints
    @GetMapping("/api/need-variables")
    public ResponseEntity<List<NeedVariableDTO>> getAllNeedVariables() {
        return ResponseEntity.ok(variableService.getAllNeedVariables());
    }

    @GetMapping("/api/need-variables/{id}")
    public ResponseEntity<NeedVariableDTO> getNeedVariableById(@PathVariable UUID id) {
        return ResponseEntity.ok(variableService.getNeedVariableById(id));
    }

    @PostMapping("/api/need-variables")
    public ResponseEntity<NeedVariableDTO> createNeedVariable(@Valid @RequestBody NeedVariableDTO needVariableDTO) {
        return new ResponseEntity<>(variableService.createNeedVariable(needVariableDTO), HttpStatus.CREATED);
    }

    @PutMapping("/api/need-variables/{id}")
    public ResponseEntity<NeedVariableDTO> updateNeedVariable(@PathVariable UUID id, @Valid @RequestBody NeedVariableDTO needVariableDTO) {
        return ResponseEntity.ok(variableService.updateNeedVariable(id, needVariableDTO));
    }

    @DeleteMapping("/api/need-variables/{id}")
    public ResponseEntity<Void> deleteNeedVariable(@PathVariable UUID id) {
        variableService.deleteNeedVariable(id);
        return ResponseEntity.noContent().build();
    }

    // Priority Variable endpoints
    @GetMapping("/api/priority-variables")
    public ResponseEntity<List<PriorityVariableDTO>> getAllPriorityVariables() {
        return ResponseEntity.ok(variableService.getAllPriorityVariables());
    }

    @GetMapping("/api/priority-variables/{id}")
    public ResponseEntity<PriorityVariableDTO> getPriorityVariableById(@PathVariable UUID id) {
        return ResponseEntity.ok(variableService.getPriorityVariableById(id));
    }

    @PostMapping("/api/priority-variables")
    public ResponseEntity<PriorityVariableDTO> createPriorityVariable(@Valid @RequestBody PriorityVariableDTO priorityVariableDTO) {
        return new ResponseEntity<>(variableService.createPriorityVariable(priorityVariableDTO), HttpStatus.CREATED);
    }

    @PutMapping("/api/priority-variables/{id}")
    public ResponseEntity<PriorityVariableDTO> updatePriorityVariable(@PathVariable UUID id, @Valid @RequestBody PriorityVariableDTO priorityVariableDTO) {
        return ResponseEntity.ok(variableService.updatePriorityVariable(id, priorityVariableDTO));
    }

    @DeleteMapping("/api/priority-variables/{id}")
    public ResponseEntity<Void> deletePriorityVariable(@PathVariable UUID id) {
        variableService.deletePriorityVariable(id);
        return ResponseEntity.noContent().build();
    }
}