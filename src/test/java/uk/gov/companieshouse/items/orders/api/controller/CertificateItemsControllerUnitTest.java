package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.core.Is.is;

/**
 * Unit/integration tests the {@link CertificateItemsController} class.
 */
@AutoConfigureMockMvc
@WebMvcTest
class CertificateItemsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test certificate item creation")
    void createCertificateItemCreatesCertificateItem() throws Exception {

        // Given
        final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
        newCertificateItemDTO.setCompanyNumber("1234");
        final ItemCosts costs = new ItemCosts();
        costs.setDiscountApplied("1");
        costs.setIndividualItemCost("2");
        costs.setPostageCost("3");
        costs.setTotalCost("4");
        newCertificateItemDTO.setItemCosts(costs);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        newCertificateItemDTO.setItemOptions(options);

        final CertificateItemDTO createdCertificateItemDTO = new CertificateItemDTO();
        createdCertificateItemDTO.setCompanyNumber(newCertificateItemDTO.getCompanyNumber());
        createdCertificateItemDTO.setKind("certificate");
        createdCertificateItemDTO.setDescriptionIdentifier("certificate");
        createdCertificateItemDTO.setItemCosts(costs);
        createdCertificateItemDTO.setItemOptions(options);
        createdCertificateItemDTO.setPostalDelivery(true);

        // When and Then
        mockMvc.perform(post("/certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCertificateItemDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdCertificateItemDTO)))
                .andExpect(jsonPath("$.item_options.cert_inc", is(true)))
                .andExpect(jsonPath("$.item_options.cert_shar", is(true)))
                .andExpect(jsonPath("$.item_options.cert_dissliq", is(false)))
                .andExpect(jsonPath("$.postal_delivery", is(true)));

        // Then
        // TODO: Verify can retrieve equivalent entity

    }

}