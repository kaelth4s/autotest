package ru.kaelth4s.tests.db.repository;

import jakarta.persistence.metamodel.SingularAttribute;
import ru.kaelth4s.tests.db.base.Repository;
import ru.kaelth4s.tests.db.model.Project;
import ru.kaelth4s.tests.db.model.Project_;

public final class ProjectRepository extends Repository<Project> {
    public ProjectRepository() {
        super(Project.class);
    }

    public Project getProjectByName(String name) {
        return findBy(Project_.NAME, name).orElseThrow(() -> new IllegalArgumentException("Cannot find project by name: %s".formatted(name)));
    }
}