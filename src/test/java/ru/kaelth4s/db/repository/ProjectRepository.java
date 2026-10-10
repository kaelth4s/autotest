package ru.kaelth4s.db.repository;

import ru.kaelth4s.db.base.Repository;
import ru.kaelth4s.db.model.Project;

public final class ProjectRepository extends Repository<Project> {

    private static final String NAME_FIELD = "name";

    public ProjectRepository() {
        super(Project.class);
    }

    public Project getOrCreate(Project project) {
        return findOrCreate(NAME_FIELD, project.getName(), () -> project);
    }
}