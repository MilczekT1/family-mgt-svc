package pl.konradboniecki.budget.familymanagement.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.exceptions.InvitationNotFoundException;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false"
)
public class InvitationServiceTests {

    @Autowired
    private TestRestTemplate rest;
    @MockBean
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationService invitationService;

    @LocalServerPort
    private int port;
    private String baseUrl;

    @BeforeAll
    public void beforeAll() {
        baseUrl = "http://localhost:" + port;
        assertThat(rest.getForEntity(baseUrl + "/actuator/health", String.class)
                .getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void given_valid_params_when_findOneInStrictMode_found_then_return_invitation() {
        // Given:
        String mailTestHolder = "test@email.com";
        Long familyIdHolder = 5L;
        Map<String, String> params = new HashMap<>();
        params.put("email", mailTestHolder);
        params.put("familyId", Long.toString(familyIdHolder));
        params.put("id", Long.toString(1L));
        Invitation preparedInvitation = new Invitation()
                .setEmail(mailTestHolder)
                .setFamilyId(familyIdHolder);
        // When:
        when(invitationRepository.findByEmailAndFamilyId(mailTestHolder, 5L))
                .thenReturn(Optional.of(preparedInvitation));
        Invitation retrievedInvitation = invitationService.findOneBy(params, true);
        // Then:
        assertThat(retrievedInvitation).isEqualToComparingFieldByField(preparedInvitation);
    }

    @Test
    public void given_invalid_params_when_findOneInStrictMode_then_throw() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("email", "some_mail");
        params.put("id", Long.toString(1L));
        // When:
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findOneBy(params, true), BadRequestException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void given_valid_params_when_findOneInStrictMode_not_found_then_throw() {
        // Given:
        String mailTestHolder = "test@email.com";
        Long familyIdHolder = 5L;
        Map<String, String> params = new HashMap<>();
        params.put("email", mailTestHolder);
        params.put("familyId", Long.toString(familyIdHolder));
        params.put("id", Long.toString(1L));
        // When:
        when(invitationRepository.findByEmailAndFamilyId(mailTestHolder, 5L))
                .thenReturn(Optional.empty());
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findOneBy(params, true), InvitationNotFoundException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(InvitationNotFoundException.class);
    }

    @Test
    public void given_valid_params_when_findOneInNonStrictMode_found_then_return_invitation() {
        // Given:
        String mailTestHolder = "test@email.com";
        Long idHolder = 5L;
        Map<String, String> params = new HashMap<>();
        params.put("id", Long.toString(idHolder));
        Invitation preparedInvitation = new Invitation()
                .setEmail(mailTestHolder)
                .setFamilyId(idHolder);
        // When:
        when(invitationRepository.findById(idHolder))
                .thenReturn(Optional.of(preparedInvitation));
        Invitation retrievedInvitation = invitationService.findOneBy(params, false);
        // Then:
        assertThat(retrievedInvitation).isEqualToComparingFieldByField(preparedInvitation);
    }

    @Test
    public void given_invalid_params_when_findOneInNonStrictMode_then_throw() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("email", "some_mail");
        params.put("familyId", Long.toString(3L));
        // When:
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findOneBy(params, false), BadRequestException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void given_valid_params_when_findOneInNonStrictMode_not_found_then_throw() {
        // Given:
        Long idHolder = 5L;
        Map<String, String> params = new HashMap<>();
        params.put("id", Long.toString(idHolder));

        // When:
        when(invitationRepository.findById(idHolder))
                .thenReturn(Optional.empty());
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findOneBy(params, false), InvitationNotFoundException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(InvitationNotFoundException.class);
    }

    @Test
    public void given_more_than_one_param_when_findAllBy_then_throw() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("email", "test@mail.com");
        params.put("familyId", Long.toString(5L));

        // When:
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findAllBy(params), BadRequestException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void given_valid_param_when_findAllBy_email_then_return_invitations() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("email", "test@mail.com");
        Invitation invitation1 = new Invitation()
                .setId(1L)
                .setEmail("test@mail.com");
        Invitation invitation2 = new Invitation()
                .setId(2L)
                .setEmail("test@mail.com");
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        // When:
        when(invitationRepository.findAllByEmail("test@mail.com"))
                .thenReturn(list);
        List<Invitation> retrievedList = invitationService.findAllBy(params);
        // Then:
        assertThat(retrievedList).isNotNull();
        assertThat(retrievedList.size()).isEqualTo(2);
        assertThat(retrievedList).containsExactly(invitation1, invitation2);
    }

    @Test
    public void given_valid_param_when_findAllBy_familyId_then_return_invitations() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("familyId", Long.toString(5L));
        Invitation invitation1 = new Invitation()
                .setId(1L)
                .setFamilyId(1L)
                .setEmail("test1@mail.com");
        Invitation invitation2 = new Invitation()
                .setId(2L)
                .setFamilyId(2L)
                .setEmail("test2@mail.com");
        List<Invitation> list = new ArrayList<>();
        list.add(invitation1);
        list.add(invitation2);
        // When:
        when(invitationRepository.findAllByFamilyId(5L))
                .thenReturn(list);
        List<Invitation> retrievedList = invitationService.findAllBy(params);
        // Then:
        assertThat(retrievedList).isNotNull();
        assertThat(retrievedList.size()).isEqualTo(2);
        assertThat(retrievedList).containsExactly(invitation1, invitation2);
    }

    @Test
    public void given_invalid_param_when_findAllBy_familyId_then_throw() {
        // Given:
        Map<String, String> params = new HashMap<>();
        params.put("invalid_param", Long.toString(5L));
        // When:
        Throwable throwable = catchThrowableOfType(
                () -> invitationService.findAllBy(params), BadRequestException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }
}
