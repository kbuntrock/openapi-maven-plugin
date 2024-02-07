package io.github.kbuntrock.configuration.attribute_getters.tag.name;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;
import io.github.kbuntrock.model.Tag;

import java.util.Optional;

public class ApiConfigurationTagNameGetter extends AbstractTagNameGetter {
    private final ApiConfiguration apiConfiguration;

    public ApiConfigurationTagNameGetter(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    public Optional<String> getTagName(final Tag tag) {
        String returnValue = tag.getName();
        for(final Substitution substitution : apiConfiguration.getTag().getSubstitutions()) {
            returnValue = returnValue.replaceAll(substitution.getRegex(), substitution.getSubstitute());
        }
        return Optional.of(returnValue);
    }

}
