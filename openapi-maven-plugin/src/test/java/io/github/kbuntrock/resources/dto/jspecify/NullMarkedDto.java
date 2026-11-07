package io.github.kbuntrock.resources.dto.jspecify;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class NullMarkedDto {

	public String defaultValue;

	public @Nullable String nullableValue;

	public int primitiveValue;

	public @Nullable NullUnmarkedNestedDto nested;

	@NullUnmarked
	public static class NullUnmarkedNestedDto {

		public String nestedDefaultValue;

		public @NonNull String nestedNonNullValue;

		public @Nullable InheritedScopeNestedDto deepNested;

		public static class InheritedScopeNestedDto {

			public String deepNestedDefaultValue;
		}
	}
}
