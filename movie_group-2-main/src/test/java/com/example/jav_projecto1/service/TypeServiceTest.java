package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.Type;
import com.example.jav_projecto1.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TypeService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class TypeServiceTest {
    // Mocked dependency
    @Mock
    private TypeRepository typeRepository;
    // Inject mocks into TypeService
    @InjectMocks
    private TypeService typeService;

    private Type type;

    /**
     * Prepare common test data before each test.
     */
    @BeforeEach
    void setUp() {
        type = new Type();
        type.setTypeId(1);
        type.setTypeName("Action");
    }

    /**
     * Test getAllTypes should return a list of types.
     */
    @Test
    void testGetAllTypes() {
        when(typeRepository.findAll()).thenReturn(List.of(type));
        List<Type> result = typeService.getAllTypes();
        assertEquals(1, result.size());
        assertEquals("Action", result.getFirst().getTypeName());
    }

    /**
     * Test getTypeById should return type if found.
     */
    @Test
    void testGetTypeById_Found() {
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        Optional<Type> result = typeService.getTypeById(1);
        assertTrue(result.isPresent());
        assertEquals("Action", result.get().getTypeName());
    }

    /**
     * Test getTypeById should return empty if not found.
     */
    @Test
    void testGetTypeById_NotFound() {
        when(typeRepository.findById(2)).thenReturn(Optional.empty());
        Optional<Type> result = typeService.getTypeById(2);
        assertFalse(result.isPresent());
    }

    /**
     * Test createType should save and return the type.
     */
    @Test
    void testCreateType() {
        when(typeRepository.save(any(Type.class))).thenReturn(type);
        Type saved = typeService.createType(type);
        assertNotNull(saved);
        assertEquals("Action", saved.getTypeName());
    }

    /**
     * Test updateType should update and return the type if found.
     */
    @Test
    void testUpdateType_Found() {
        Type updatedDetails = new Type();
        updatedDetails.setTypeName("Comedy");
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        when(typeRepository.save(any(Type.class))).thenReturn(updatedDetails);
        Optional<Type> result = typeService.updateType(1, updatedDetails);
        assertTrue(result.isPresent());
        assertEquals("Comedy", result.get().getTypeName());
    }

    /**
     * Test updateType should return empty if type not found.
     */
    @Test
    void testUpdateType_NotFound() {
        Type updatedDetails = new Type();
        updatedDetails.setTypeName("Comedy");
        when(typeRepository.findById(2)).thenReturn(Optional.empty());
        Optional<Type> result = typeService.updateType(2, updatedDetails);
        assertFalse(result.isPresent());
    }

    /**
     * Test deleteType should return true if type exists and is deleted.
     */
    @Test
    void testDeleteType_Success() {
        when(typeRepository.existsById(1)).thenReturn(true);
        doNothing().when(typeRepository).deleteById(1);
        boolean result = typeService.deleteType(1);
        assertTrue(result);
    }

    /**
     * Test deleteType should return false if type does not exist.
     */
    @Test
    void testDeleteType_NotFound() {
        when(typeRepository.existsById(2)).thenReturn(false);
        boolean result = typeService.deleteType(2);
        assertFalse(result);
    }
}