package uk.gov.companieshouse.items.orders.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Configuration
@Component
@PropertySource("classpath:costs.properties")
@ConfigurationProperties(prefix = "costs")
@Validated
public class CostsConfig {

    @Min(1)
    private int standardCost;
    @Min(1)
    private int sameDayCost;
    @Min(1)
    private int standardDiscount;
    @Min(1)
    private int sameDayDiscount;

    public int getStandardCost() {
        return standardCost;
    }

    public void setStandardCost(int standardCost) {
        this.standardCost = standardCost;
    }

    public int getSameDayCost() {
        return sameDayCost;
    }

    public void setSameDayCost(int sameDayCost) {
        this.sameDayCost = sameDayCost;
    }

    public int getStandardDiscount() {
        return standardDiscount;
    }

    public void setStandardDiscount(int standardDiscount) {
        this.standardDiscount = standardDiscount;
    }

    public int getSameDayDiscount() {
        return sameDayDiscount;
    }

    public void setSameDayDiscount(int sameDayDiscount) {
        this.sameDayDiscount = sameDayDiscount;
    }

}
