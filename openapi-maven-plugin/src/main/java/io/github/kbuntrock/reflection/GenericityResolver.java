package io.github.kbuntrock.reflection;

import com.google.common.reflect.TypeToken;
import io.github.kbuntrock.utils.Logger;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.maven.plugin.logging.Log;

/**
 * Used to resolve genericity on classes which are DataObjects. (usually endpoints)
 *
 * @author Kévin Buntrock
 */
public final class GenericityResolver {

	private final Log logger = Logger.INSTANCE.getLogger();

	private Map<ResolvingKey, Type> resolvedCache = new HashMap<>();

	/**
	 * Given a context class, resolve the given type. (mostly useful for parametrized types)
	 * @param contextClass the context class
	 * @param typeToResolve the type to be resolved (ex : List<T>)
	 * @return the resolved type (ex: List<MyDto>)
	 */
	public Type resolve(final Class contextClass, final Type typeToResolve) {
		Type resolved = resolvedCache.computeIfAbsent(new ResolvingKey(contextClass, typeToResolve), k -> {
			if(logger.isDebugEnabled()) {
				logger.debug("Computing cache key : "+contextClass.getSimpleName()+ " : "+typeToResolve.toString());
			}
			return TypeToken.of(contextClass).resolveType(typeToResolve).getType();
		});
		if(logger.isDebugEnabled()) {
			logger.debug("Returning resolved type for : "+contextClass.getSimpleName()+ " : "+typeToResolve.toString() + " -> "+resolved);
		}
		return resolved;
	}

	private static class ResolvingKey {
		private final Class contextClass;
		private final Type typeToResolve;

		public ResolvingKey(Class contextClass, Type typeToResolve) {
			this.contextClass = contextClass;
			this.typeToResolve = typeToResolve;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}
			ResolvingKey duo = (ResolvingKey) o;
			return Objects.equals(contextClass, duo.contextClass) && Objects.equals(typeToResolve, duo.typeToResolve);
		}

		@Override
		public int hashCode() {
			return Objects.hash(contextClass, typeToResolve);
		}
	}

}
