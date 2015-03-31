package com.github.leifoolsen.jerseyjpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class FeedTeller {
    @Id
    @Column(length=36)
    private String id;

    @Version
    private Long version;

    public int teller;
    public int maxverdi;

    public FeedTeller() {}
    public FeedTeller(String id, int teller, int maxverdi) {
        this.id = id;
        this.teller = teller;
        this.maxverdi = maxverdi;
    }

    @Override
    public String toString() {
        return "FeedTeller{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", teller=" + teller +
                ", maxverdi=" + maxverdi +
                '}';
    }
}
