package pl.konradboniecki.budget.familymanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@Table
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "thisNameSuxInHibernate5")
    @GenericGenerator(name = "thisNameSuxInHibernate5", strategy = "increment")
    @Column(name = "invitation_id")
    private Long id;

    @Column(name = "family_id")
    private Long familyId;
    @Column(name = "email")
    private String email;
    @Column(name = "invitation_code")
    private String invitationCode;
    @Column(name = "apply_time", insertable = false)
    private Instant applyTime;
    @Column(name = "new_user")
    private Boolean registeredStatus;
}
