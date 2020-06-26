package pl.konradboniecki.budget.familymanagement.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.familymanagement.exceptions.InvitationNotFoundException;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.chassis.exceptions.BadRequestException;
import pl.konradboniecki.chassis.exceptions.InternalServerErrorException;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class InvitationService {

    private InvitationRepository invitationRepository;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    //TODO: test it (jpa)
    public void removeInvitationByIdOrThrow(Long invitationId) {
        try {
            invitationRepository.deleteById(invitationId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvitationNotFoundException("Invitation with id: " + invitationId + " not found.");
        }
    }

    //TODO: test it (jpa)
    public Invitation saveInvitationOrThrow(Invitation postedinvitation) {
        try {
            return invitationRepository.save(postedinvitation);
        } catch (PersistenceException e) {
            log.error("Failed to save Invitation");
            throw new InternalServerErrorException("Something bad happened, check your posted data", e);
        }
    }

    public List<Invitation> findAllBy(@NonNull Map<String, String> params) {
        if (params.size() != 1) {
            throw new BadRequestException("Should be only 1 parameter: \"email\" or \"familyId\"");
        }
        if (params.containsKey("email")) {
            return findAllByEmail(params.get("email"));
        } else if (params.containsKey("familyId")) {
            return findAllByFamilyId(Long.valueOf(params.get("familyId")));
        } else {
            throw new BadRequestException("Should be only 1 parameter: \"email\" or \"familyId\"");
        }
    }

    public List<Invitation> findAllByEmail(String email) {
        return invitationRepository.findAllByEmail(email);
    }

    public List<Invitation> findAllByFamilyId(Long familyId) {
        return invitationRepository.findAllByFamilyId(familyId);
    }

    public Invitation findOneBy(Map<String, String> parameters, Boolean strictMode) {
        if (strictMode) {
            return findOneInStrictModeByOrThrow(parameters);
        } else {
            return findOneInNonStrictModeByOrThrow(parameters);
        }
    }

    private Invitation findOneInStrictModeByOrThrow(Map<String, String> parameters) throws BadRequestException {
        parametersIncludesOrThrow(parameters, "email", "familyId");
        Optional<Invitation> invitation = invitationRepository
                .findByEmailAndFamilyId(parameters.get("email"), Long.valueOf(parameters.get("familyId")));
        if (invitation.isPresent()) {
            return invitation.get();
        } else {
            String msg = "invitation with email: " + parameters.get("email") + " and familyId: " + Long.valueOf(parameters.get("familyId")) + " not found.";
            throw new InvitationNotFoundException(msg);
        }

    }

    private Invitation findOneInNonStrictModeByOrThrow(Map<String, String> parameters) throws BadRequestException {
        parametersIncludesOrThrow(parameters, "id");
        Optional<Invitation> invitation = invitationRepository.findById(Long.valueOf(parameters.get("id")));
        if (invitation.isPresent()) {
            return invitation.get();
        } else {
            throw new InvitationNotFoundException("invitation with id: " + parameters.get("id") + " not found.");
        }
    }

    private boolean parametersIncludesOrThrow(Map<String, String> currentParams, String... desiredParams) throws BadRequestException {
        for (String param : desiredParams) {
            if (!currentParams.containsKey(param)) {
                throw new BadRequestException("Parameter: \"" + param + "\" is missing.");
            }
        }
        return true;
    }
}
