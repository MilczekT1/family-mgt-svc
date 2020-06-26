package pl.konradboniecki.budget.familymanagement.contractbases;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import pl.konradboniecki.budget.familymanagement.Application;
import pl.konradboniecki.budget.familymanagement.model.Family;
import pl.konradboniecki.budget.familymanagement.service.FamilyRepository;

import java.util.Optional;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class AccountManagementClientBase {

    @MockBean
    private FamilyRepository familyRepository;

    @LocalServerPort
    int port;

    @Before
    public void setUpMocks() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
        mockAbsentFamily();
        mockFamily();
    }

    private void mockAbsentFamily() {
        when(familyRepository.findById(5L))
                .thenReturn(Optional.empty());
    }

    private void mockFamily() {
        Family familyToReturn = new Family()
                .setId(1L)
                .setOwnerId(2L)
                .setBudgetId(3L)
                .setMaxMembers(5)
                .setTitle("testTitle");
        when(familyRepository.findById(1L))
                .thenReturn(Optional.of(familyToReturn));
    }
}
