package com.v1690117.fdb.core;

import com.v1690117.fdb.core.services.ObjectService;
import com.v1690117.fdb.core.services.TypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@SpringBootTest(classes = IntegrationTestConf.class)
public class CrossCuttingIntegrationTest {
    @Autowired
    private TypeService ts;
    @Autowired
    private ObjectService objectService;

    @Test
    public void createObjectAndConnect() {
        ts.addType("Requirement");
        ts.setTypeAttributes(
                "Requirement",
                Map.of(
                        "Title", "Title",
                        "Text", "Text"
                )
        );
        var firstId = objectService.create(
                "Requirement",
                Map.of(
                        "Title", "First",
                        "Text", "Base requirement"
                )
        );
        var first = objectService.read(firstId, null);
        assertThat(first.get("Title")).isEqualTo("First");
        assertThat(first.get("Text")).isEqualTo("Base requirement");
        assertThat(first.get("type")).isEqualTo("Requirement");

        var secondId = objectService.create(
                "Requirement",
                Map.of(
                        "Title", "Second",
                        "Text", "Another requirement"
                )
        );

        objectService.connect(firstId, secondId, "Sub Requirement");
        first = objectService.read(firstId, null);
        assertThat(first.get("from[Sub Requirement].to.id")).isNotNull();
    }
}
