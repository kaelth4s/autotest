package ru.kaelth4s.db.dto;

import ru.kaelth4s.db.model.Author;
import ru.kaelth4s.db.model.Project;

public record DbTestData(
        Author author,
        Project project,
        Integer idsLimit
) {
    public static DbTestData from(DbTestData json) {
        return new DbTestData(
                json.author,
                json.project,
                json.idsLimit
        );
    }
}