package uk.gov.companieshouse.items.orders.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.service.CompanyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemsApiApplicationTest {

	private static final String COMPANY_NUMBER = "00006400";
	private static final String COMPANY_NOT_FOUND_ERROR =
			"Error getting company name for company number " + COMPANY_NUMBER;

	@MockBean
	private CompanyService companyService;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	@DisplayName("Application context loads successfully")
	void contextLoads() {
		// No implementation required here to test that context loads.
	}

	@Test
	@DisplayName("Create rejects read only company name")
	void createCertificateItemRejectsReadOnlyCompanyName() {
		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setCompanyName("Phillips & Daughters");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "company_name: must be null");
	}

    @Test
    @DisplayName("Create rejects missing company number")
    void createCertificateItemRejectsMissingCompanyNumber() {
        // Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setCompanyNumber(null);

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "company_number: must not be null");
    }

	@Test
	@DisplayName("Create does not reject missing item costs")
	void createCertificateItemDoesNotRejectMissingItemCosts() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromObject(newCertificateItemDTO))
				.exchange()
				.expectStatus().isCreated();

	}

	@Test
	@DisplayName("Create rejects missing item options")
	void createCertificateItemRejectsMissingItemOptions() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setItemOptions(null);

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "item_options: must not be null");
	}

	@Test
	@DisplayName("Create rejects missing quantity")
	void createCertificateItemRejectsMissingQuantity() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setQuantity(0); // 0 is default value when value not specified

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "quantity: must be greater than or equal to 1");
	}

	@Test
	@DisplayName("Create rejects read only item costs")
	void createCertificateItemRejectsReadOnlyItemCosts() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		final List<ItemCosts> costs = new ArrayList<>();
		final ItemCosts cost = new ItemCosts();
		cost.setDiscountApplied("1");
		cost.setItemCost("2");
		cost.setCalculatedCost("4");
		costs.add(cost);
		newCertificateItemDTO.setItemCosts(costs);


		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "item_costs: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description")
	void createCertificateItemRejectsReadOnlyDescription() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setDescription("description text");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "description: must be null");
	}


	@Test
	@DisplayName("Create rejects read only description identifier")
	void createCertificateItemRejectsReadOnlyDescriptionIdentifier() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setDescriptionIdentifier("description identifier text");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "description_identifier: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description values")
	void createCertificateItemRejectsReadOnlyDescriptionValues() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setDescriptionValues(new HashMap<>());

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "description_values: must be null");
	}

	@Test
	@DisplayName("Create rejects read only id")
	void createCertificateItemRejectsReadOnlyId() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setId("TEST_ID");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "id: must be null in a create item request");
	}

	@Test
	@DisplayName("Create rejects read only postage cost")
	void createCertificateItemRejectsReadOnlyPostageCost() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setPostageCost("0");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "postage_cost: must be null");
	}


	@Test
	@DisplayName("Create rejects read only total item cost")
	void createCertificateItemRejectsReadOnlyTotalItemCost() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setTotalItemCost("100");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemDTO, "total_item_cost: must be null");
	}

	@Test
	@DisplayName("Create rejects missing X-Request-ID")
	void createCertificateItemRejectsMissingRequestId() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.body(fromObject(newCertificateItemDTO))
				.exchange()
				.expectStatus().isBadRequest();

	}

	@Test
	@DisplayName("Create reports company not found as bad request")
	void createCertificateItemReportsCompanyNotFoundAsBadRequest() {
		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		when(companyService.getCompanyName(COMPANY_NUMBER)).
				thenThrow(new ResponseStatusException(BAD_REQUEST, COMPANY_NOT_FOUND_ERROR));

		// When and Then
		postCreateRequestAndExpectBadRequestResponseStatusError(newCertificateItemDTO, COMPANY_NOT_FOUND_ERROR);
	}

	/**
	 * Utility method that posts the create certificate item request, asserts a bad request status response and an
	 * expected validation error message.
	 * @param itemToCreate the DTO representing the certificate item to be requested
	 * @param expectedError expected validation error message
	 */
	private void postCreateRequestAndExpectBadRequestResponse(final CertificateItemDTO itemToCreate,
															  final String expectedError) {
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromObject(itemToCreate))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo("BAD_REQUEST")
				.jsonPath("$.errors[0]").isEqualTo(expectedError);
	}

	/**
	 * Utility method that posts the create certificate item request, asserts a bad request status response and an
	 * expected error message.
	 * @param itemToCreate the DTO representing the certificate item to be requested
	 * @param expectedError expected error message
	 */
	private void postCreateRequestAndExpectBadRequestResponseStatusError(final CertificateItemDTO itemToCreate,
																		 final String expectedError) {
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromObject(itemToCreate))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo("400")
				.jsonPath("$.message").isEqualTo(expectedError);
	}

	/**
	 * Factory method that produces a DTO for a valid create item request payload.
	 * @return a valid item DTO
	 */
	private CertificateItemDTO createValidNewItem() {
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber(COMPANY_NUMBER);
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setDeliveryTimescale(STANDARD);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);
		return newCertificateItemDTO;
	}

}
