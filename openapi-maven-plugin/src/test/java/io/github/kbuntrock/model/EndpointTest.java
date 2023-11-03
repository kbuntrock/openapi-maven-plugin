package io.github.kbuntrock.model;

import org.junit.jupiter.api.Test;

import static io.github.kbuntrock.model.OperationType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EndpointTest {
    @Test
    public void testCompareTo() {
        // Create some sample Endpoint objects for testing
        Endpoint endpoint1 = new Endpoint();
        endpoint1.setPath("/api/resource");
        endpoint1.setType(GET);
        endpoint1.setName("Resource GET");

        Endpoint endpoint2 = new Endpoint();
        endpoint2.setPath("/api/resource");
        endpoint2.setType(POST);
        endpoint2.setName("Resource POST");

        Endpoint endpoint3 = new Endpoint();
        endpoint3.setPath("/api/resource");
        endpoint3.setType(GET);
        endpoint3.setName("Resource GET");

        // Compare endpoint1 and endpoint2
        assertThat(endpoint1).isLessThan(endpoint2);

        // Compare endpoint2 and endpoint1 (reverse order)
        assertThat(endpoint2).isGreaterThan(endpoint1);

        // Compare endpoint1 and endpoint3 (should be equal)
        assertThat(endpoint1).isEqualByComparingTo(endpoint3);
    }
}
