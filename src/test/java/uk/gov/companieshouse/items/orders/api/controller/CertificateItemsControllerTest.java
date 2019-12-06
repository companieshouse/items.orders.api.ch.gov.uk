package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.TOKEN_REQUEST_ID_VALUE;

/**
 * Unit/integration tests the {@link CertificateItemsController} class.
 */
@AutoConfigureMockMvc
@SpringBootTest
class CertificateItemsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CertificateItemRepository repository;

    private static final String EXPECTED_ITEM_ID = "CHS00000000000000001";
    private static final int QUANTITY = 5;

    @AfterEach
    void tearDown() {
        repository.findById(EXPECTED_ITEM_ID).ifPresent(repository::delete);
    }

    @Test
    @DisplayName("Successfully creates certificate item")
    void createCertificateItemSuccessfullyCreatesCertificateItem() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber("1234");
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setId(EXPECTED_ITEM_ID);
        expectedItem.setCompanyNumber(newItem.getCompanyNumber());
        expectedItem.setKind("certificate");
        expectedItem.setDescriptionIdentifier("certificate");
        final ItemCosts costs = new ItemCosts();
        costs.setDiscountApplied("1");
        costs.setIndividualItemCost("2");
        costs.setPostageCost("3");
        costs.setTotalCost("4");
        expectedItem.setItemCosts(costs);
        expectedItem.setItemOptions(options);
        expectedItem.setPostalDelivery(true);
        expectedItem.setQuantity(QUANTITY);

        // When and Then
        final ResultActions outcome = mockMvc.perform(post("/certificates")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andExpect(jsonPath("$.item_options.cert_inc", is(true)))
                .andExpect(jsonPath("$.item_options.cert_shar", is(true)))
                .andExpect(jsonPath("$.item_options.cert_dissliq", is(false)))
                .andExpect(jsonPath("$.postal_delivery", is(true)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemSavedCorrectly(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item that fails validation")
    void createCertificateItemFailsToCreateCertificateItem() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList("company_number: must not be null"));

        // When and Then
        final ResultActions outcome = mockMvc.perform(post("/certificates")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    /**
     * Verifies that the item assumed to have been created by the create item POST request can be retrieved
     * from the database using its expected ID value.
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemSavedCorrectly(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(expectedItemId));
    }

    /**
     * Verifies that the item that could have been created by the create item POST request cannot in fact be retrieved
     * from the database.
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemWasNotSaved(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(false));
    }

}