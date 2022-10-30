package io.github.kbuntrock;

import io.github.kbuntrock.resources.dto.Authority;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Used to ease up development
 *
 * @author Kevin Buntrock
 */
public class ReflectionUtilsTest {

	private List<List<Set<Authority>>> o1;
	private List<Authority> o2;
	private Authority o3;

	@Test
	public void test() throws ClassNotFoundException, NoSuchFieldException {

		final Field field = ReflectionUtilsTest.class.getDeclaredField("o1");
		final Class<?> myClass = field.getType();
		final ParameterizedType parameterizedType = ((ParameterizedType) field.getGenericType());
		Assertions.assertNotNull(parameterizedType);
		Assertions.assertEquals("interface java.util.List", parameterizedType.getRawType().toString());

		final Field field2 = ReflectionUtilsTest.class.getDeclaredField("o2");
		final Class<?> myClass2 = field2.getType();
		final ParameterizedType parameterizedType2 = ((ParameterizedType) field2.getGenericType());
		Assertions.assertNotNull(parameterizedType2);
		Assertions.assertEquals("interface java.util.List", parameterizedType2.getRawType().toString());
		Assertions.assertEquals("class io.github.kbuntrock.resources.dto.Authority",
			parameterizedType2.getActualTypeArguments()[0].toString());

		final Field field3 = ReflectionUtilsTest.class.getDeclaredField("o3");
		final Class<?> myClass3 = field3.getType();
		final Type parameterizedType3 = field3.getGenericType();
		Assertions.assertEquals("class io.github.kbuntrock.resources.dto.Authority", parameterizedType3.toString());
	}
}
