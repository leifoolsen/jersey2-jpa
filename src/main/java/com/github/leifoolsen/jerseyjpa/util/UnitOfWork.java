package com.github.leifoolsen.jerseyjpa.util;

import javax.persistence.EntityManager;

public interface UnitOfWork {
    EntityManager begin();
    void end();
}
