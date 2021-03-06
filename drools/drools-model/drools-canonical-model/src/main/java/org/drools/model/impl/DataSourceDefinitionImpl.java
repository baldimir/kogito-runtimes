package org.drools.model.impl;

import org.drools.model.DataSourceDefinition;
import org.drools.model.DeclarationSource;

public class DataSourceDefinitionImpl implements DeclarationSource, DataSourceDefinition {

    public static final DataSourceDefinition DEFAULT = new DataSourceDefinitionImpl("DEFAULT", false);

    private final String name;
    private final boolean observable;

    public DataSourceDefinitionImpl(String name, boolean observable) {
        this.name = name;
        this.observable = observable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isObservable() {
        return observable;
    }
}
