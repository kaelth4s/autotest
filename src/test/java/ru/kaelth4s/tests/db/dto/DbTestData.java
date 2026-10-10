package ru.kaelth4s.tests.db.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.kaelth4s.tests.db.model.Author;
import ru.kaelth4s.tests.db.model.TestModel;

public record DbTestData(
        TestModel test
) {
    @JsonCreator
    public static DbTestData from(
            @JsonProperty("test") TestModel test
    ) {
        return new DbTestData(
                test
        );
    }
}