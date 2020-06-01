package io.cucumber.core.plugin;

import io.cucumber.core.internal.gherkin.ast.Examples;
import io.cucumber.core.internal.gherkin.ast.TableRow;
import net.serenitybdd.cucumber.CucumberWithSerenity;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LineFilters {

    private Map<URI, Set<Integer>> lineFilters;

    public LineFilters() {
        lineFilters = newLineFilters();
    }

    public static LineFilters forCurrentContext() {
        return new LineFilters();
    }

    public Optional<URI> getURIForFeaturePath(URI featurePath) {
        return lineFilters.keySet().stream()
                .filter(uri -> featurePath.equals(uri))
                .findFirst();
    }

    private Map<URI, Set<Integer>> newLineFilters() {
        Map<URI, Set<Integer>> lineFiltersFromRuntime = CucumberWithSerenity.currentRuntimeOptions().getLineFilters();
        if (lineFiltersFromRuntime == null) {
            return new HashMap<>();
        } else {
            return lineFiltersFromRuntime;
        }
    }

    public Set<Integer> getLineNumbersFor(URI featurePath) {
        return lineFilters.get(featurePath);
    }


    public boolean examplesAreNotExcluded(Examples examples, URI featurePath) {
        if (lineFilters.isEmpty()) {
            return true;
        } else {
            Optional<URI> uriForFeaturePath = getURIForFeaturePath(featurePath);

            return uriForFeaturePath.filter(
                    uri -> examples.getTableBody().stream()
                            .anyMatch(
                                    row -> lineFilters.get(uri).contains(row.getLocation().getLine()))
            ).isPresent();
        }
    }

    public boolean tableRowIsNotExcludedBy(TableRow tableRow, URI featurePath) {
        if (lineFilters.isEmpty()) {
            return true;
        } else {
            Optional<URI> uriForFeaturePath = getURIForFeaturePath(featurePath);
            return uriForFeaturePath.filter(uri -> lineFilters.get(uri).contains(tableRow.getLocation().getLine())).isPresent();
        }
    }

}
