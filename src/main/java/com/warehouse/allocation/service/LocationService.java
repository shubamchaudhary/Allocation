package com.warehouse.allocation.service;

import com.warehouse.allocation.dto.LocationDTO;
import com.warehouse.allocation.exception.ResourceNotFoundException;
import com.warehouse.allocation.model.Location;
import com.warehouse.allocation.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LocationDTO getLocationById(UUID id) {
        return locationRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public LocationDTO getLocationByCode(String code) {
        return locationRepository.findByCode(code)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with code: " + code));
    }

    @Transactional(readOnly = true)
    public List<LocationDTO> getLocationsByType(Location.LocationType type) {
        return locationRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocationDTO> getStoresWithMinShipQuantity(Integer quantity) {
        return locationRepository.findStoresWithMinShipQuantity(quantity).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = mapToEntity(locationDTO);
        Location savedLocation = locationRepository.save(location);
        return mapToDTO(savedLocation);
    }

    @Transactional
    public LocationDTO updateLocation(UUID id, LocationDTO locationDTO) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));

        existingLocation.setCode(locationDTO.getCode());
        existingLocation.setName(locationDTO.getName());
        existingLocation.setType(locationDTO.getType());
        existingLocation.setLatitude(locationDTO.getLatitude());
        existingLocation.setLongitude(locationDTO.getLongitude());
        existingLocation.setMinShipQuantity(locationDTO.getMinShipQuantity());

        Location updatedLocation = locationRepository.save(existingLocation);
        return mapToDTO(updatedLocation);
    }

    @Transactional
    public void deleteLocation(UUID id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }

    private LocationDTO mapToDTO(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .code(location.getCode())
                .name(location.getName())
                .type(location.getType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .minShipQuantity(location.getMinShipQuantity())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    private Location mapToEntity(LocationDTO locationDTO) {
        return Location.builder()
                .id(locationDTO.getId())
                .code(locationDTO.getCode())
                .name(locationDTO.getName())
                .type(locationDTO.getType())
                .latitude(locationDTO.getLatitude())
                .longitude(locationDTO.getLongitude())
                .minShipQuantity(locationDTO.getMinShipQuantity())
                .build();
    }
}