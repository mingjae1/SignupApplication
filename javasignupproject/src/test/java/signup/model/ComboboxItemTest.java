package signup.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComboboxItem model class
 */
class ComboboxItemTest {

    @Test
    void testConstructorAndGetters() {
        ComboboxItem item = new ComboboxItem("Test Item", 123);
        assertEquals("Test Item", item.getName());
        assertEquals(123, item.getId());
    }

    @Test
    void testToString() {
        ComboboxItem item = new ComboboxItem("Display Name", 456);
        assertEquals("Display Name", item.toString());
    }

    @Test
    void testMultipleItems() {
        ComboboxItem item1 = new ComboboxItem("Item 1", 1);
        ComboboxItem item2 = new ComboboxItem("Item 2", 2);
        
        assertEquals("Item 1", item1.getName());
        assertEquals(1, item1.getId());
        assertEquals("Item 2", item2.getName());
        assertEquals(2, item2.getId());
    }

    @Test
    void testWithKoreanCharacters() {
        ComboboxItem item = new ComboboxItem("서울캠퍼스", 1);
        assertEquals("서울캠퍼스", item.getName());
        assertEquals("서울캠퍼스", item.toString());
        assertEquals(1, item.getId());
    }

    @Test
    void testWithEmptyString() {
        ComboboxItem item = new ComboboxItem("", 0);
        assertEquals("", item.getName());
        assertEquals(0, item.getId());
    }
}
