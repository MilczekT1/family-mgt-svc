package pl.konradboniecki.budget.familymanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class FamilyTests {

    @Test
    public void givenNullProperties_whenMergeFamilies_thenDontCopyNulls() {
        // Given:
        Family family1 = new Family()
                .setId(1L)
                .setOwnerId(1L)
                .setBudgetId(1L)
                .setTitle("testTitle")
                .setMaxMembers(5);
        Family familyWithNulls = new Family()
                .setMaxMembers(null);

        // When:
        family1.mergeFamilies(familyWithNulls);

        //Then:
        assertAll(
                () -> assertEquals(1L, family1.getId().longValue()),
                () -> assertEquals(1L, family1.getOwnerId().longValue()),
                () -> assertEquals(1L, family1.getBudgetId().longValue()),
                () -> assertEquals("testTitle", family1.getTitle()),
                () -> assertEquals(5L, family1.getMaxMembers().longValue())
        );
    }

    @Test
    public void givenValidProperties_whenMergeFamilies_thenOverrideProperties() {
        // Given:
        Family family1 = new Family()
                .setId(1L)
                .setOwnerId(1L)
                .setBudgetId(1L)
                .setTitle("testTitle")
                .setMaxMembers(5);
        Family familyWithNulls = new Family()
                .setId(2L)
                .setOwnerId(2L)
                .setBudgetId(2L)
                .setTitle("testTitle123")
                .setMaxMembers(10);

        // When:
        family1.mergeFamilies(familyWithNulls);

        //Then:
        assertAll(
                () -> assertEquals(2L, family1.getId().longValue()),
                () -> assertEquals(2L, family1.getOwnerId().longValue()),
                () -> assertEquals(2L, family1.getBudgetId().longValue()),
                () -> assertEquals("testTitle123", family1.getTitle()),
                () -> assertEquals(10L, family1.getMaxMembers().longValue())
        );
    }
}
