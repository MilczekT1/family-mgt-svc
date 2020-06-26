package pl.konradboniecki.budget.familymanagement.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.konradboniecki.budget.familymanagement.model.Invitation;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, Long> {
    /**
     * /find-one?email=XX&familyId=YY&strict=true
     *
     * @param email
     * @param familyId
     */
    Optional<Invitation> findByEmailAndFamilyId(String email, Long familyId);

    /**
     * find-one?id=XX
     *
     * @param id
     */
    Optional<Invitation> findById(Long id);

    /**
     * /api/family-invitations/find-all?familyId=XX
     *
     * @param id
     */
    List<Invitation> findAllByFamilyId(Long id);

    /**
     * /api/family-invitations/find-all?email=XX
     *
     * @param email
     */
    List<Invitation> findAllByEmail(String email);

    /**
     * /api/family-invitations/{id}
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * /api/family-invitations
     *
     * @param invitation
     * @return
     */
    Invitation save(Invitation invitation);
}
