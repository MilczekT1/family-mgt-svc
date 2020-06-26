package pl.konradboniecki.budget.familymanagement.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.konradboniecki.budget.familymanagement.model.Family;

import java.util.Optional;

@Repository
public interface FamilyRepository extends CrudRepository<Family, Long> {
    Optional<Family> findById(Long id);

    Optional<Family> findByOwnerId(Long id);

    Family save(Family family);

    void deleteById(Long id);

    boolean existsById(Long id);

    @Query(value = "SELECT (max_members-members_amount) from family_members where family_id =?1 ", nativeQuery = true)
    Integer countFreeSlotsInFamilyWithId(Long familyId);
//
//   @Modifying
//   @Transactional
//   @Query(value="UPDATE family SET budget_id = ?1 WHERE family_id = ?2", nativeQuery=true)
//   void setBudgetId(Long budgetId, Long familyId);
}
