package pl.konradboniecki.budget.familymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.familymanagement.model.FamilySlotSummary;
import pl.konradboniecki.budget.familymanagement.service.FamilyService;

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    private FamilyService familyService;

    @Autowired
    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @PostMapping
    public ResponseEntity<Family> saveFamily(@RequestBody Family familyToSave) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.saveFamilyOrThrow(familyToSave));
    }

    @PutMapping
    public ResponseEntity<Family> editFamily(@RequestBody Family updatedFamily) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.updateFamilyOrThrow(updatedFamily));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Family> findFamily(
            @PathVariable("id") Long id,
            @RequestParam(name = "idType", required = false, defaultValue = "id") String findBy) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.findFamily(id, findBy));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<FamilySlotSummary> countSlots(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(familyService.getSlotSummary(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamily(@PathVariable("id") Long id) {
        familyService.deleteFamilyById(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
