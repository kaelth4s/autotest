package ru.kaelth4s.tests.db.repository;

import ru.kaelth4s.tests.db.base.Repository;
import ru.kaelth4s.tests.db.model.Attachment;

public final class AttachmentRepository extends Repository<Attachment> {
    public AttachmentRepository() {
        super(Attachment.class);
    }

    public Attachment create(Attachment attachment) {
        return save(attachment);
    }
}