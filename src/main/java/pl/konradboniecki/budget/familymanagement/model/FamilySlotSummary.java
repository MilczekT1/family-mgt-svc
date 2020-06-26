package pl.konradboniecki.budget.familymanagement.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FamilySlotSummary {
    private Long familyId;
    private Integer freeSlots;
    private Integer maxSlots;
}
