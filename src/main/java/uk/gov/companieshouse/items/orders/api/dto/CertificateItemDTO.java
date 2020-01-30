package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import javax.validation.constraints.NotNull;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemDTO extends ItemDTO {

    @NotNull
    @JsonProperty("item_options")
    private CertificateItemOptions itemOptions;

    @NotNull
    @JsonProperty("company_name")
    private String companyName;

    @NotNull
    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    public CertificateItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

}
