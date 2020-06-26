package pl.konradboniecki.budget.familymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.konradboniecki.budget.familymanagement.exceptions.FamilyNotFoundException;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.familymanagement.model.FamilySlotSummary;

import java.util.Optional;

//TODO: test it
@Service
public class FamilyService {

    private FamilyRepository familyRepository;

    @Autowired
    public FamilyService(FamilyRepository familyRepository) {
        this.familyRepository = familyRepository;
    }

    public Family findFamily(Long id, String findBy) {
        switch (findBy) {
            case "id":
                return findFamilyById(id);
            case "ownerId":
                return findFamilyByOwnerId(id);
            default:
                //TODO: test message
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid argument findBy=" + findBy + ", it should be \"id\" or \"ownerId\"");
        }
    }

    private Family findFamilyById(Long id) {
        Optional<Family> family = familyRepository.findById(id);
        if (family.isPresent()) {
            return family.get();
        } else {
            throw new FamilyNotFoundException("Family with id: " + id + " not found");
        }
    }

    private Family findFamilyByOwnerId(Long id) {
        Optional<Family> family = familyRepository.findByOwnerId(id);
        if (family.isPresent()) {
            return family.get();
        } else {
            throw new FamilyNotFoundException("Family not found for owner with id: " + id);
        }
    }

    public void deleteFamilyById(Long id) {
        if (existsById(id)) {
            familyRepository.deleteById(id);
        } else {
            throw new FamilyNotFoundException("Family with id: " + id + " not found");
        }
    }

    private boolean existsById(Long id) {
        return familyRepository.existsById(id);
    }

    public FamilySlotSummary getSlotSummary(Long id) {
        Optional<Family> familyOptional = familyRepository.findById(id);

        if (familyOptional.isPresent()) {
            return new FamilySlotSummary()
                    .setFamilyId(id)
                    .setFreeSlots(familyRepository.countFreeSlotsInFamilyWithId(id))
                    .setMaxSlots(familyOptional.get().getMaxMembers());
        } else {
            throw new FamilyNotFoundException("Family with id: " + id + " not found");
        }
    }

    public Family updateFamilyOrThrow(Family putFamily) {
        Optional<Family> family = familyRepository.findById(putFamily.getId());
        if (family.isPresent()) {
            return familyRepository.save(family.get().mergeFamilies(putFamily));
        } else {
            throw new FamilyNotFoundException("Family with id: " + putFamily.getId() + " not found");
        }
    }

    public Family saveFamilyOrThrow(Family postedFamily) {
        Optional<Family> family = familyRepository.findByOwnerId(postedFamily.getOwnerId());
        if (family.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            return familyRepository.save(postedFamily);
        }
    }
}
