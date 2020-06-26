package pl.konradboniecki.budget.familymanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Table
@Entity
public class Family implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "thisNameSucksInHibernate5")
    @GenericGenerator(name = "thisNameSucksInHibernate5", strategy = "increment")
    @Column(name = "family_id")
    private Long id;
    @JsonProperty(required = true)
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "budget_id")
    private Long budgetId;
    @Column(name = "title")
    private String title;
    @Column(name = "max_members")
    private Integer maxMembers;

    public Family() {
        setMaxMembers(5);
    }

    public Family mergeFamilies(Family family) {
        if (family.getId() != null) {
            setId(family.getId());
        }
        if (family.getOwnerId() != null) {
            setOwnerId(family.getOwnerId());
        }
        if (family.getBudgetId() != null) {
            setBudgetId(family.getBudgetId());
        }
        if (family.getTitle() != null) {
            setTitle(family.getTitle());
        }
        if (family.getMaxMembers() != null) {
            setMaxMembers(family.getMaxMembers());
        }
        return this;
    }

//    public Family(String jsonObjectName, ObjectNode json) {
//        this();
//        if (jsonObjectName == null)
//            throw new NullPointerException("Family has been initialized with null");
//        if (jsonObjectName.equals(""))
//            throw new IllegalArgumentException("Family has been initialized with either \"\"");
//
//        JsonNode familyNode = json.get(jsonObjectName);
//        if (familyNode.has("id")) setId(familyNode.get("id").asLong());
//        if (familyNode.has("ownerId")) setOwnerId(familyNode.get("ownerId").asLong());
//        if (familyNode.has("budgetId")) setBudgetId(familyNode.get("budgetId").asLong(0));
//        if (familyNode.has("title")) setTitle(familyNode.get("title").asText());
//        if (familyNode.has("maxMembers")) setMaxMembers(familyNode.get("maxMembers").asInt());
//    }
}
