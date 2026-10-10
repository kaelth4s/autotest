package ru.kaelth4s.db.repository;

import ru.kaelth4s.db.base.Repository;
import ru.kaelth4s.db.model.Author;

public final class AuthorRepository extends Repository<Author> {
    private static final String LOGIN_FIELD = "login";

    public AuthorRepository() {
        super(Author.class);
    }

    public Author getOrCreate(Author author) {
        return findOrCreate(LOGIN_FIELD, author.getLogin(), () -> author);
    }
}