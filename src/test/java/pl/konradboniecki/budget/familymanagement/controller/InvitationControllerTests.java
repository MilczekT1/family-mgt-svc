package pl.konradboniecki.budget.familymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.familymanagement.service.InvitationRepository;
import pl.konradboniecki.budget.familymanagement.service.InvitationService;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.MOCK,
        properties = "spring.cloud.config.enabled=false"
)
@AutoConfigureMockMvc
public class InvitationControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper;

    private String basicAuthHeaderValue;

    @BeforeAll
    public void setUp() {
        basicAuthHeaderValue = chassisSecurityBasicAuthHelper.getBasicAuthHeaderValue();
    }

    // GET /api/family-invitations/find-one

    @Test
    public void when_invitation_is_found_in_strict_mode_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        Invitation invitation = new Invitation()
                .setEmail("test@mail.com").setFamilyId(5L);
        when(invitationRepository.findByEmailAndFamilyId("test@mail.com", 5L))
                .thenReturn(Optional.of(invitation));
        // Then:
        mockMvc.perform(
                get("/api/family-invitations/find-one?strict=true&email=test@mail.com&familyId=5")
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    @Test
    public void when_invitation_is_found_in_nonStrict_mode_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        Invitation invitation = new Invitation()
                .setId(5L);
        when(invitationRepository.findById(5L))
                .thenReturn(Optional.of(invitation));
        // Then:
        mockMvc.perform(get("/api/family-invitations/find-one?id=5")
                .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    // GET /api/family-invitations/find-all

    @Test
    public void when_invitations_are_found_by_email_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        Invitation invitation1 = new Invitation()
                .setEmail("test1@mail.com")
                .setId(1L);
        Invitation invitation2 = new Invitation()
                .setEmail("test1@mail.com")
                .setId(2L);
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        when(invitationRepository.findAllByEmail("test1@mail.com"))
                .thenReturn(list);
        // Then:
        mockMvc.perform(
                get("/api/family-invitations/find-all?email=test1@mail.com")
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    @Test
    public void when_invitations_are_found_by_familyId_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        Invitation invitation1 = new Invitation()
                .setEmail("test1@mail.com")
                .setId(1L)
                .setFamilyId(1L);
        Invitation invitation2 = new Invitation()
                .setEmail("test2@mail.com")
                .setId(2L)
                .setFamilyId(1L);
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        when(invitationRepository.findAllByFamilyId(1L))
                .thenReturn(list);
        // Then:
        mockMvc.perform(
                get("/api/family-invitations/find-all?familyId=1")
                        .header("Authorization", basicAuthHeaderValue))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    // POST /api/family-invitations

    @Test
    public void when_invitation_is_saved_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        Invitation invitationInRequestBody = new Invitation()
                .setFamilyId(1L)
                .setEmail("test@mail.com")
                .setInvitationCode("e227de87-1eee-497b-93f7-1e503f4e9875")
                .setRegisteredStatus(false);
        Invitation invitationInResponseBody = new Invitation()
                .setFamilyId(1L)
                .setEmail("test@mail.com")
                .setInvitationCode("e227de87-1eee-497b-93f7-1e503f4e9875")
                .setRegisteredStatus(false)
                .setId(1L);
        when(invitationRepository.save(invitationInRequestBody))
                .thenReturn(invitationInResponseBody);
        // Then:
        mockMvc.perform(post("/api/family-invitations")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", basicAuthHeaderValue)
                .content(new ObjectMapper().writeValueAsString(invitationInResponseBody)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON.toString()));
    }

    // DELETE /api/family-invitations/{invitationId}

    @Test
    public void when_invitation_is_removed_then_response_status_and_headers_are_correct() throws Exception {
        // Given:
        doNothing().when(invitationRepository).deleteById(3L);
        // Then:
        mockMvc.perform(
                delete("/api/family-invitations/3")
                        .header("Authorization", basicAuthHeaderValue)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
