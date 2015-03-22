package com.github.leifoolsen.jerseyjpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(indexes = {@Index(name = "publisher_name_index", columnList = "name")})
public class Publisher {
    @Id
    @Column(length=36)
    private String id = UUID.randomUUID().toString();

    @Version
    private Long version;

    @NotNull
    @Size(min=5, max=5)
    @Column(length = 5, unique = true)
    private String code;

    private String name;

    protected Publisher() {}

    public Publisher(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public String getId() { return id; }

    public String getCode() { return code; }

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;

        if (!code.equals(publisher.code)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
