package pl.konradboniecki.budget.familymanagement.exceptions;

import pl.konradboniecki.chassis.exceptions.ResourceNotFoundException;

public class InvitationNotFoundException extends ResourceNotFoundException {

    public InvitationNotFoundException(String message) {
        super(message);
    }
}
