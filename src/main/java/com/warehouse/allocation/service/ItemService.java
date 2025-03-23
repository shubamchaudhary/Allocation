package com.warehouse.allocation.service;

import com.warehouse.allocation.dto.ItemDTO;
import com.warehouse.allocation.exception.ResourceNotFoundException;
import com.warehouse.allocation.model.Item;
import com.warehouse.allocation.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemDTO getItemById(UUID id) {
        return itemRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public ItemDTO getItemByStyleColor(String styleColor) {
        return itemRepository.findByStyleColor(styleColor)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with styleColor: " + styleColor));
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsByStyle(String style) {
        return itemRepository.findByStyle(style).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsByType(Item.ItemType type) {
        return itemRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDTO createItem(ItemDTO itemDTO) {
        Item item = mapToEntity(itemDTO);
        Item savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }

    @Transactional
    public ItemDTO updateItem(UUID id, ItemDTO itemDTO) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        existingItem.setStyleColor(itemDTO.getStyleColor());
        existingItem.setStyle(itemDTO.getStyle());
        existingItem.setItemClass(itemDTO.getItemClass());
        existingItem.setDescription(itemDTO.getDescription());
        existingItem.setType(itemDTO.getType());
        existingItem.setPackRatio(itemDTO.getPackRatio());

        Item updatedItem = itemRepository.save(existingItem);
        return mapToDTO(updatedItem);
    }

    @Transactional
    public void deleteItem(UUID id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    private ItemDTO mapToDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .styleColor(item.getStyleColor())
                .style(item.getStyle())
                .itemClass(item.getItemClass())
                .description(item.getDescription())
                .type(item.getType())
                .packRatio(item.getPackRatio())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private Item mapToEntity(ItemDTO itemDTO) {
        return Item.builder()
                .id(itemDTO.getId())
                .styleColor(itemDTO.getStyleColor())
                .style(itemDTO.getStyle())
                .itemClass(itemDTO.getItemClass())
                .description(itemDTO.getDescription())
                .type(itemDTO.getType())
                .packRatio(itemDTO.getPackRatio())
                .build();
    }
}