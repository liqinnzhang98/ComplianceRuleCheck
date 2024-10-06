package com.apromore.compliance_centre.process.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.apromore.compliance_centre.process.ProcessModel;

public class AttributesTest {
    @Test
    public void testToString() {
        // Test for INT
        assertEquals("INT", AttributeDataType.INT.toString());

        // Test for FLOAT
        assertEquals("FLOAT", AttributeDataType.FLOAT.toString());

        // Test for TEXT
        assertEquals("TEXT", AttributeDataType.TEXT.toString());

        // Test for TIMESTAMP
        assertEquals("TIMESTAMP", AttributeDataType.TIMESTAMP.toString());
    }

    @Test
    public void testGetDatabaseColumnName() {
        AttributeModel attribute = new AttributeModel();

        // Test when name is null
        attribute.setName(null);
        assertEquals(null, attribute.getDatabaseColumnName());

        // Test when name is not null
        attribute.setName("exampleName");
        assertEquals("`exampleName`", attribute.getDatabaseColumnName());
    }

    @Test
    public void testGetAttributeType() {
        AttributeModel attribute = new AttributeModel();
        AttributeType expectedType = AttributeType.ATTRIBUTE;

        attribute.setType(expectedType);
        assertEquals(expectedType, attribute.getAttributeType());
    }

    public class AttributeModelTest {

    @Test
    public void testEquals() {
        // Create two AttributeModel instances with the same id
        AttributeModel attribute1 = new AttributeModel();
        attribute1.setId(1L);
        
        AttributeModel attribute2 = new AttributeModel();
        attribute2.setId(1L);
        
        assertEquals(attribute1, attribute2);
        
        // Change the id of one instance
        attribute2.setId(2L);
        assertNotEquals(attribute1, attribute2);
    }

    @Test
    public void testHashCode() {
        AttributeModel attribute1 = new AttributeModel();
        attribute1.setId(1L);
        
        AttributeModel attribute2 = new AttributeModel();
        attribute2.setId(1L);
        
        assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }

    @Test
    public void testToString() {
        AttributeModel attribute = new AttributeModel();
        attribute.setId(1L);
        attribute.setName("Name");
        attribute.setDisplayName("Display Name");
        
        String expectedToString = "AttributeModel{id=1, name='Name', displayName='Display Name', ...}";
        
        assertEquals(expectedToString, attribute.toString());
    }
    @Test
    public void testGetProcess() {
        ProcessModel process = new ProcessModel(/* Initialize with desired values */);

        AttributeModel attribute = new AttributeModel();
        attribute.setProcess(process);

        assertEquals(process, attribute.getProcess());
    }

    @Test
    public void testCanEqual() {
        AttributeModel attribute1 = new AttributeModel();
        AttributeModel attribute2 = new AttributeModel();

        assertTrue(attribute1.canEqual(attribute2));
    }

    @Test
    public void testDtoEquals() {
        AttributeDto dto1 = new AttributeDto();
        dto1.setId(1L);
        dto1.setName("Name1");
        dto1.setDisplayName("DisplayName1");

        AttributeDto dto2 = new AttributeDto();
        dto2.setId(1L);
        dto2.setName("Name1");
        dto2.setDisplayName("DisplayName1");

        assertEquals(dto1, dto2);
    }

    @Test
    public void testDtoNotEquals() {
        AttributeDto dto1 = new AttributeDto();
        dto1.setId(1L);
        dto1.setName("Name1");
        dto1.setDisplayName("DisplayName1");

        AttributeDto dto2 = new AttributeDto();
        dto2.setId(2L);
        dto2.setName("Name2");
        dto2.setDisplayName("DisplayName2");

        assertFalse(dto1.equals(dto2));
    }

    @Test
    public void testDtoToString() {
        AttributeDto dto = new AttributeDto();
        dto.setId(1L);
        dto.setName("Name1");
        dto.setDisplayName("DisplayName1");

        String expectedString = "AttributeDto(id=1, name=Name1, displayName=DisplayName1, dataType=TEXT, type=ATTRIBUTE)";
        assertEquals(expectedString, dto.toString());
    }

    @Test
    public void testDtoHashCode() {
        AttributeDto dto1 = new AttributeDto();
        dto1.setId(1L);
        dto1.setName("Name1");
        dto1.setDisplayName("DisplayName1");

        AttributeDto dto2 = new AttributeDto();
        dto2.setId(1L);
        dto2.setName("Name1");
        dto2.setDisplayName("DisplayName1");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }




}

    
}
