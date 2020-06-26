package pl.konradboniecki.budget.familymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.familymanagement.service.InvitationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/family-invitations")
public class InvitationController {

    private InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/find-one")
    public ResponseEntity<Invitation> findInvitation(
            @RequestParam(name = "strict", required = false, defaultValue = "false") Boolean strictMode,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "familyId", required = false) Long familyId,
            @RequestParam(name = "id", required = false) Long id) {

        Map<String, String> params = new HashMap<>();
        if (email != null) params.put("email", email);
        if (familyId != null) params.put("familyId", familyId.toString());
        if (id != null) params.put("id", id.toString());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invitationService.findOneBy(params, strictMode));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Invitation>> findInvitations(@RequestParam(name = "email", required = false) String email,
                                                            @RequestParam(name = "familyId", required = false) Long familyId) {
        Map<String, String> params = new HashMap<>();
        if (email != null) params.put("email", email);
        if (familyId != null) params.put("familyId", familyId.toString());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(invitationService.findAllBy(params));
    }

    @DeleteMapping("/{invitationId}")
    public ResponseEntity<String> deleteInvitation(@PathVariable Long invitationId) {
        invitationService.removeInvitationByIdOrThrow(invitationId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping
    public ResponseEntity<Invitation> saveInvitation(
            @RequestBody Invitation invitationToSave) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(invitationService.saveInvitationOrThrow(invitationToSave));
    }
}
