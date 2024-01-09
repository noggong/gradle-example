package com.example.gradle;

import org.gradle.api.artifacts.CacheableRule;
import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;

@CacheableRule
public class Slf4jSimpleRule implements ComponentMetadataRule {
    @Override
    public void execute(ComponentMetadataContext context) {
        context.getDetails().allVariants(v ->
                v.withDependencies(d -> d.removeIf(gav -> gav.getName().equals("slf4j-api"))));
    }
}
