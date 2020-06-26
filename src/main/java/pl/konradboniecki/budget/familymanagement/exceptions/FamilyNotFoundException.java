package pl.konradboniecki.budget.familymanagement.exceptions;

import pl.konradboniecki.chassis.exceptions.ResourceNotFoundException;

public class FamilyNotFoundException extends ResourceNotFoundException {

    public FamilyNotFoundException(String message) {
        super(message);
    }
}
