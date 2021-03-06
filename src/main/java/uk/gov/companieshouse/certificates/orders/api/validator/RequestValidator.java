package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.stream;
import static org.apache.commons.lang.StringUtils.isBlank;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;

/**
 * Implements common request payload validation.
 */
public class RequestValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConstants.APPLICATION_NAMESPACE);

    /**
     * Validates the options provided, returning any errors found.
     *
     * @param options   the options to be validated
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     * @return the errors found, which will be empty if the item is found to be valid
     */
    List<String> getValidationErrors(final CertificateItemOptions options, final FieldNameConverter converter) {
        final List<String> errors = new ArrayList<>();
        if (options == null) {
            return errors;
        }
        errors.addAll(getCollectionDeliveryValidationErrors(options));

        if (options.getCertificateType() == DISSOLUTION) {

            if (options.getIncludeCompanyObjectsInformation() != null) {
                errors.add(
                    "include_company_objects_information: must not exist when certificate type is dissolution");
            }
            if (options.getIncludeGoodStandingInformation() != null) {
                errors.add(
                    "include_good_standing_information: must not exist when certificate type is dissolution");
            }
            if (options.getRegisteredOfficeAddressDetails() != null) {
                errors.add(
                    "include_registered_office_address_details: must not exist when certificate type is dissolution");
            }
            if (options.getSecretaryDetails() != null) {
                errors.add(
                    "include_secretary_details: must not exist when certificate type is dissolution");
            }
            if (options.getDirectorDetails() !=null) {
                errors.add(
                    "include_director_details: must not exist when certificate type is dissolution");
            }
        }
        if (TRUE.equals(options.getIncludeEmailCopy()) &&
                (options.getDeliveryTimescale() != SAME_DAY)) {
            errors.add("include_email_copy: can only be true when delivery timescale is same_day");
        }
        errors.addAll(getValidationErrors(options.getDirectorDetails(), "director_details", converter));
        errors.addAll(getValidationErrors(options.getSecretaryDetails(), "secretary_details", converter));
        return errors;
    }

    /**
     * Validates the details provided, returning any errors found, prefixed with the supplied details field name.
     * @param details the details to be validated
     * @param detailsFieldName the field name of the details to be validated
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     * @return the resulting errors, which will be empty if the details are found to be valid
     */
    List<String> getValidationErrors(final DirectorOrSecretaryDetails details,
                                     final String detailsFieldName,
                                     final FieldNameConverter converter) {
        final List<String> errors = new ArrayList<>();
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return errors;
        }
        final Field[] fields = details.getClass().getDeclaredFields();
        final List<String> incorrectlySetFields = stream(fields)
                .filter(field ->
                        !field.getName().equals("includeBasicInformation") &&
                                field.getType().equals(Boolean.class) &&
                                isTrue(field, details))
                .map(field -> converter.toSnakeCase(field.getName()))
                .collect(Collectors.toList());
        if (!incorrectlySetFields.isEmpty()) {
            final String fieldList = incorrectlySetFields.toString().replace("[", "").replace("]", "");
            errors.add(detailsFieldName + ": " + fieldList + " must not be true when include_basic_information is false");
        }
        if (details.getIncludeDobType() != null) {
            errors.add(detailsFieldName + ": include_dob_type must not be non-null when include_basic_information is false");
        }
        return errors;
    }

    /**
     * Validates the collection delivery related fields on the options provided.
     * @param options the options to be validated
     * @return the resulting errors, which will be empty if the fields are found to be valid
     */
    List<String> getCollectionDeliveryValidationErrors(final CertificateItemOptions options) {
        final List<String> errors = new ArrayList<>();
        if (options.getDeliveryMethod() == COLLECTION) {
            if (options.getCollectionLocation() == null) {
                errors.add("collection_location: must not be null when delivery method is collection");
            }
            if (isBlank(options.getForename())) {
                errors.add("forename: must not be blank when delivery method is collection");
            }
            if (isBlank(options.getSurname())) {
                errors.add("surname: must not be blank when delivery method is collection");
            }
        }
        return errors;
    }

    /**
     * Determines whether the value of the field on the details object is equivalent to <code>true</code> or not.
     * @param field the reflective representation of the details field
     * @param details the details object
     * @return whether the field value is equivalent to <code>true</code> (<code>true</code>), or not
     * (<code>false</code>)
     */
    boolean isTrue(final Field field, final DirectorOrSecretaryDetails details) {
        Boolean include;
        try {
            include = (Boolean) new PropertyDescriptor(field.getName(), details.getClass())
                    .getReadMethod().invoke(details);
        } catch (Exception e) {
            // This should only arise should someone alter or remove the getter for a field.
            LOGGER.error("Error invoking getter for " + details.getClass().getSimpleName() + "." + field.getName(), e);
            include = null;
        }
        return TRUE.equals(include);
    }

}
