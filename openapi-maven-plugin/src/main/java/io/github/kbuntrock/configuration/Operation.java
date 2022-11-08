package io.github.kbuntrock.configuration;

import io.github.kbuntrock.model.OperationType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugins.annotations.Parameter;

public class Operation {

	@Parameter
	private List<Substitution> substitutions = new ArrayList<>();
	
	private Map<String, List<Substitution>> substitutionsMap;

	public List<Substitution> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(final List<Substitution> substitutions) {
		this.substitutions = substitutions;
	}

	/**
	 * Get substitutions by type
	 *
	 * @param type
	 * @return substitutions to apply to the operationId
	 */
	public List<Substitution> getSubstitutionsByType(final String type) {
		if(substitutionsMap == null) {
			substitutionsMap = new HashMap<>();
			for(final OperationType opType : OperationType.values()) {
				substitutionsMap.put(opType.toString().toLowerCase(), new ArrayList<>());
			}
			for(final Substitution substitution : substitutions) {
				if(substitution.getType() == null) {
					for(final List<Substitution> value : substitutionsMap.values()) {
						value.add(substitution);
					}
				} else {
					final List<Substitution> list = substitutionsMap.get(substitution.getType().toLowerCase());
					// It can only be null if there is a mistake in the configuration. It should be handled before.
					if(list != null) {
						list.add(substitution);
					}
				}
			}

		}
		return substitutionsMap.get(type);
	}
}
