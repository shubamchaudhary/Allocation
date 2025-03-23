package com.warehouse.allocation.controller;

import com.warehouse.allocation.dto.ItemDTO;
import com.warehouse.allocation.model.Item;
import com.warehouse.allocation.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable UUID id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping("/stylecolor/{styleColor}")
    public ResponseEntity<ItemDTO> getItemByStyleColor(@PathVariable String styleColor) {
        return ResponseEntity.ok(itemService.getItemByStyleColor(styleColor));
    }

    @GetMapping("/style/{style}")
    public ResponseEntity<List<ItemDTO>> getItemsByStyle(@PathVariable String style) {
        return ResponseEntity.ok(itemService.getItemsByStyle(style));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ItemDTO>> getItemsByType(@PathVariable Item.ItemType type) {
        return ResponseEntity.ok(itemService.getItemsByType(type));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        return new ResponseEntity<>(itemService.createItem(itemDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable UUID id, @Valid @RequestBody ItemDTO itemDTO) {
        return ResponseEntity.ok(itemService.updateItem(id, itemDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}