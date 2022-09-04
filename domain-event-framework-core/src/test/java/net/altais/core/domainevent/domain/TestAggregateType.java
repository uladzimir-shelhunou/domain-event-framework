package net.altais.core.domainevent.domain;

import net.vvsh.domainevent.core.domain.AggregateType;

public enum TestAggregateType implements AggregateType {

    TEST_AGGREGATE("TestAggregate");

    private final String name;

    TestAggregateType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
