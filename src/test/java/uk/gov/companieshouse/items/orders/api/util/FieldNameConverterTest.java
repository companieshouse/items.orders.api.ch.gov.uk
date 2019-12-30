package uk.gov.companieshouse.items.orders.api.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests the {@link FieldNameConverter} class.
 */
@SpringBootTest
class FieldNameConverterTest {

    @Autowired
    private FieldNameConverter converterUnderTest;

    @Test
    void toSnakeCaseWorksAsExpected() {
        assertThat(converterUnderTest.toSnakeCase("itemCosts"), is("item_costs"));
        assertThat(converterUnderTest.toSnakeCase("item"), is("item"));
        assertThat(converterUnderTest.toSnakeCase("certIncConLast"), is("cert_inc_con_last"));
    }
}
