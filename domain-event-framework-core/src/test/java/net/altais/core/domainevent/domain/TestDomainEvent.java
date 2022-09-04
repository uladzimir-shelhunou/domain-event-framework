package net.altais.core.domainevent.domain;

import lombok.Data;
import net.vvsh.domainevent.core.domain.DomainEvent;

@Data
public class TestDomainEvent implements DomainEvent {

    private String message;
    //
}
