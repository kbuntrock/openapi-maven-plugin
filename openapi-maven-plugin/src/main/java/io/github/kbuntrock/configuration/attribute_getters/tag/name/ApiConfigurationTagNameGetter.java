package io.github.kbuntrock.configuration.attribute_getters.tag.name;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;

import java.util.Optional;

public class ApiConfigurationTagNameGetter extends AbstractTagNameGetter {
    private final String baseName;
    private final ApiConfiguration apiConfiguration;

    public ApiConfigurationTagNameGetter(String baseName, ApiConfiguration apiConfiguration) {
        this.baseName = baseName;
        this.apiConfiguration = apiConfiguration;
    }

    public Optional<String> getTagName() {
        String returnValue = baseName;
        for(final Substitution substitution : apiConfiguration.getTag().getSubstitutions()) {
            returnValue = returnValue.replaceAll(substitution.getRegex(), substitution.getSubstitute());
        }
        return Optional.of(returnValue);
    }

}
