package uk.gov.companieshouse.items.orders.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.items.orders.api.config.ApplicationConfiguration;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests the {@link PatchMerger} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(PatchMergerTest.Config.class)
class PatchMergerTest {

    @Configuration
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ApplicationConfiguration().objectMapper();
        }

        @Bean
        PatchMerger patchMerger() {
            return new PatchMerger(objectMapper());
        }

        @Bean
        TestMergePatchFactory patchFactory() {
            return new TestMergePatchFactory(objectMapper());
        }
    }

    private static final String ORIGINAL_COMPANY_NUMBER = "1234";
    private static final String CORRECTED_COMPANY_NUMBER = "1235";
    private static final boolean ORIGINAL_POSTAL_DELIVERY = true;
    private static final boolean CORRECTED_POSTAL_DELIVERY = false;
    private static final int ORIGINAL_QUANTITY = 20;
    private static final int CORRECTED_QUANTITY = 2;

    private static final String ORIGINAL_ADDITIONAL_INFO = "We have our reasons.";
    private static final boolean ORIGINAL_CERT_ACC = true;
    private static final boolean ORIGINAL_CERT_ARTS = true;

    private static final String CORRECTED_ADDITIONAL_INFO = "We have our top secret reasons.";
    private static final boolean CORRECTED_CERT_ACC = false;

    @Autowired
    private PatchMerger patchMergerUnderTest;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestMergePatchFactory patchFactory;

    @Test
    @DisplayName("Unpopulated source string property does not overwrite populated target field")
    void unpopulatedSourceStringLeavesTargetIntact() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setCompanyNumber(ORIGINAL_COMPANY_NUMBER);
        final CertificateItem empty = new CertificateItem();

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(empty), original, CertificateItem.class);

        // Then
        assertThat(patched.getCompanyNumber(), is(ORIGINAL_COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Unpopulated source boolean property does not overwrite populated target field")
    void unpopulatedSourceBooleanLeavesTargetIntact() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setPostalDelivery(ORIGINAL_POSTAL_DELIVERY);
        final CertificateItem empty = new CertificateItem();

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(empty), original, CertificateItem.class);

        // Then
        assertThat(patched.isPostalDelivery(), is(ORIGINAL_POSTAL_DELIVERY));
    }

    @Test
    @DisplayName("Unpopulated source integer property does not overwrite populated target field")
    void unpopulatedIntegerPropertyDoesNotOverwrite() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setQuantity(ORIGINAL_QUANTITY);
        final CertificateItem empty = new CertificateItem();

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(empty), original, CertificateItem.class);

        // Then
        assertThat(patched.getQuantity(), is(ORIGINAL_QUANTITY));
    }

    @Test
    @DisplayName("Root level string property is propagated correctly")
    void sourceRootLevelStringPropertyPropagated() throws IOException  {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setCompanyNumber(ORIGINAL_COMPANY_NUMBER);
        final CertificateItem delta = new CertificateItem();
        delta.setCompanyNumber(CORRECTED_COMPANY_NUMBER);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.getCompanyNumber(), is(CORRECTED_COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Root level boolean property is propagated correctly")
    void sourceRootLevelBooleanPropertyPropagated() throws IOException  {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setPostalDelivery(ORIGINAL_POSTAL_DELIVERY);
        final CertificateItem delta = new CertificateItem();
        delta.setPostalDelivery(CORRECTED_POSTAL_DELIVERY);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.isPostalDelivery(), is(CORRECTED_POSTAL_DELIVERY));
    }

    @Test
    @DisplayName("Root level integer property is propagated correctly")
    void sourceRootLevelIntegerPropertyPropagated() throws IOException  {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setQuantity(ORIGINAL_QUANTITY);
        final CertificateItem delta = new CertificateItem();
        delta.setQuantity(CORRECTED_QUANTITY);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.getQuantity(), is(CORRECTED_QUANTITY));
    }

    @Test
    @DisplayName("Nested level properties are propagated correctly")
    void sourceNestedLevelPropertiesPropagated() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        final CertificateItemOptions originalOptions = new CertificateItemOptions();
        originalOptions.setAdditionalInformation(ORIGINAL_ADDITIONAL_INFO);
        originalOptions.setCertAcc(ORIGINAL_CERT_ACC);
        originalOptions.setCertArts(ORIGINAL_CERT_ARTS);
        original.setItemOptions(originalOptions);

        final CertificateItem delta = new CertificateItem();
        final CertificateItemOptions deltaOptions = new CertificateItemOptions();
        deltaOptions.setAdditionalInformation(CORRECTED_ADDITIONAL_INFO);
        deltaOptions.setCertAcc(CORRECTED_CERT_ACC);
        delta.setItemOptions(deltaOptions);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.getItemOptions().getAdditionalInformation(), is(CORRECTED_ADDITIONAL_INFO));
        assertThat(patched.getItemOptions().isCertAcc(), is(CORRECTED_CERT_ACC));
        assertThat(patched.getItemOptions().isCertArts(), is(ORIGINAL_CERT_ARTS));
    }

}