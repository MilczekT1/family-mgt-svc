package pl.konradboniecki.budget.familymanagement.contractbases;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.familymanagement.service.FamilyRepository;
import pl.konradboniecki.budget.familymanagement.service.InvitationRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class MvcClientBase {

    @LocalServerPort
    int port;

    @MockBean
    private FamilyRepository familyRepository;
    @MockBean
    private InvitationRepository invitationRepository;

    @Before
    public void setUpMocks() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
        mockAbsentFamily();
        mockFamilySlotSummary();
        mockFamilyDeletion();
        mockFamilyByOwnerId();
        mockSaveFamily();

        mockInvitationDeletion();
        mockAbsenceOfInvitationDuringDeletion();
        mockInvitationsByFamilyId();
        mockInvitationsByEmail();
        mockInvitationByEmailAndFamilyId();
        mockInvitationById();
        mockSaveInvitation();
    }

    private void mockAbsentFamily() {
        when(familyRepository.findById(5L))
                .thenReturn(Optional.empty());
    }

    private void mockFamilySlotSummary() {
        Family familyToReturn = new Family()
                .setId(1L)
                .setOwnerId(3L)
                .setBudgetId(3L)
                .setTitle("testTitle")
                .setMaxMembers(5);
        when(familyRepository.findById(1L)).thenReturn(Optional.of(familyToReturn));
        when(familyRepository.countFreeSlotsInFamilyWithId(1L)).thenReturn(4);
    }

    private void mockFamilyDeletion() {
        doNothing().when(familyRepository).deleteById(100L);
        when(familyRepository.existsById(100L)).thenReturn(true);
    }

    private void mockInvitationDeletion() {
        doNothing().when(invitationRepository).deleteById(1L);
    }

    private void mockAbsenceOfInvitationDuringDeletion() {
        doThrow(EmptyResultDataAccessException.class).
                when(invitationRepository).deleteById(5L);
    }

    private void mockInvitationsByFamilyId() {
        Invitation invitation1 = new Invitation()
                .setId(6L)
                .setFamilyId(1L)
                .setEmail("mail_1@mail.com")
                .setInvitationCode("34b7a194-b0d3-47f7-8aef-1d64caefcdf4")
                .setApplyTime(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegisteredStatus(true);
        Invitation invitation2 = new Invitation()
                .setId(7L)
                .setFamilyId(1L)
                .setEmail("mail_2@mail.com")
                .setInvitationCode("c04a8005-cb67-46de-a4dc-e4f84d26faf3")
                .setApplyTime(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegisteredStatus(false);
        ArrayList<Invitation> invitations = new ArrayList<>();
        invitations.add(invitation1);
        invitations.add(invitation2);
        when(invitationRepository.findAllByFamilyId(1L)).thenReturn(invitations);
        when(invitationRepository.findAllByFamilyId(5L)).thenReturn(Collections.emptyList());
    }

    private void mockInvitationsByEmail() {
        String emailWithoutInvitations = "email@without-invitations.com";
        String emailWithInvitations = "email@with-invitations.com";

        Invitation invitation1 = new Invitation()
                .setId(6L)
                .setFamilyId(6L)
                .setEmail(emailWithInvitations)
                .setInvitationCode("34b7a194-b0d3-47f7-8aef-1d64caefcdf4")
                .setApplyTime(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegisteredStatus(true);
        Invitation invitation2 = new Invitation()
                .setId(7L)
                .setFamilyId(null)
                .setEmail(emailWithInvitations)
                .setInvitationCode("c04a8005-cb67-46de-a4dc-e4f84d26faf3")
                .setApplyTime(Instant.parse("2019-06-16T10:22:54.246625Z"))
                .setRegisteredStatus(true);
        ArrayList<Invitation> invitations = new ArrayList<>();
        invitations.add(invitation1);
        invitations.add(invitation2);
        when(invitationRepository.findAllByEmail(emailWithInvitations)).thenReturn(invitations);
        when(invitationRepository.findAllByEmail(emailWithoutInvitations)).thenReturn(Collections.emptyList());
    }

    private void mockFamilyByOwnerId() {
        Family family = new Family()
                .setId(9L)
                .setOwnerId(1L)
                .setBudgetId(9L)
                .setTitle("testTitle")
                .setMaxMembers(5);
        when(familyRepository.findByOwnerId(1L)).thenReturn(Optional.of(family));
        when(familyRepository.findByOwnerId(5L)).thenReturn(Optional.empty());
    }

    private void mockInvitationByEmailAndFamilyId() {
        Invitation invitation = new Invitation()
                .setId(9L)
                .setFamilyId(1L)
                .setEmail("email@with-invitations.com")
                .setRegisteredStatus(true)
                .setApplyTime(Instant.now())
                .setInvitationCode(UUID.randomUUID().toString());
        when(invitationRepository.findByEmailAndFamilyId("email@with-invitations.com", 1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.findByEmailAndFamilyId("email@without-invitations.com", 5L)).thenReturn(Optional.empty());
    }

    private void mockInvitationById() {
        Invitation inv = new Invitation()
                .setId(1L)
                .setFamilyId(9L)
                .setInvitationCode(UUID.randomUUID().toString())
                .setApplyTime(Instant.now())
                .setRegisteredStatus(true)
                .setEmail("random@email.com");
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(invitationRepository.findById(5L)).thenReturn(Optional.empty());
    }

    private void mockSaveFamily() {
        Family family = new Family()
                .setOwnerId(100L)
                .setBudgetId(100L)
                .setTitle("testTitle")
                .setMaxMembers(5)
                .setId(null);
        when(familyRepository.findByOwnerId(100L)).thenReturn(Optional.empty());
        when(familyRepository.findByOwnerId(101L)).thenReturn(Optional.of(new Family()));
        when(familyRepository.findById(100L)).thenReturn(Optional.of(new Family()));
        when(familyRepository.save(any(Family.class))).thenReturn(family.setId(100L));
    }

    private void mockSaveInvitation() {
        Invitation invitation = new Invitation()
                .setFamilyId(5L)
                .setEmail("test@mail2.com")
                .setInvitationCode("ebdd12b3-aedc-4b32-9518-cc71263c0775")
                .setRegisteredStatus(false)
                .setApplyTime(Instant.now());
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation.setId(99L));
    }
}
