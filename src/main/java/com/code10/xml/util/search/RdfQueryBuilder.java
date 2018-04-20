package com.code10.xml.util.search;

import com.code10.xml.model.RdfTriple;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

import java.util.ArrayList;
import java.util.List;

public class RdfQueryBuilder {

    private List<RdfTriple> operands;

    private List<Operator> operators;

    private List<RdfTriple> bindings;

    public RdfQueryBuilder(RdfTriple triple) {
        this(triple, null, null, null);
    }

    public RdfQueryBuilder(RdfTriple triple, String subject, String predicate, String object) {
        operands = new ArrayList<>();
        operands.add(triple);

        bindings = new ArrayList<>();
        bindings.add(new RdfTriple(subject != null ? subject : "s", predicate != null ? predicate : "p", object != null ? object : "o"));

        operators = new ArrayList<>();
    }

    public RdfQueryBuilder and(RdfTriple triple) {
        return and(triple, null, null, null);
    }

    public RdfQueryBuilder and(RdfTriple triple, String subject, String predicate, String object) {
        operands.add(triple);
        operators.add(Operator.AND);

        bindings.add(new RdfTriple(subject != null ? subject : "s", predicate != null ? predicate : "p", object != null ? object : "o"));

        return this;
    }

    public RdfQueryBuilder or(RdfTriple triple) {
        return or(triple, null, null, null);
    }

    public RdfQueryBuilder or(RdfTriple triple, String subject, String predicate, String object) {
        operands.add(triple);
        operators.add(Operator.OR);

        bindings.add(new RdfTriple(subject != null ? subject : "s", predicate != null ? predicate : "p", object != null ? object : "o"));

        return this;
    }

    public SPARQLQueryDefinition makeQuery(SPARQLQueryManager queryManager) {
        return makeQuery(queryManager, null);
    }

    public SPARQLQueryDefinition makeQuery(SPARQLQueryManager queryManager, String graph) {
        return makeQuery("*", queryManager, graph);
    }

    public SPARQLQueryDefinition makeQuery(String selectExpression, SPARQLQueryManager queryManager, String graph) {
        final StringBuilder builder;

        if (graph != null) {
            builder = new StringBuilder("SELECT DISTINCT " + selectExpression + " FROM <" + graph + "> WHERE { ");
        } else {
            builder = new StringBuilder("SELECT DISTINCT " + selectExpression + " WHERE { ");
        }

        for (int i = 0; i < operands.size(); i++) {

            if (i != 0) {

                switch (operators.get(i - 1)) {
                    case OR:
                        builder.append("UNION ");
                        break;
                    case AND:
                        break;
                }
            }
            builder.append(String.format("{ %s %s %s . } ",
                    operands.get(i).getSubject() != null ? String.format("<%s>", operands.get(i).getSubject()) : "?" + bindings.get(i).getSubject(),
                    operands.get(i).getPredicate() != null ? String.format("<%s>", operands.get(i).getPredicate()) : "?" + bindings.get(i).getPredicate(),
                    operands.get(i).getObject() != null ? String.format("<%s>", operands.get(i).getObject()) : "?" + bindings.get(i).getObject()));
        }

        builder.append("}");

        return queryManager.newQueryDefinition(builder.toString());
    }
}
