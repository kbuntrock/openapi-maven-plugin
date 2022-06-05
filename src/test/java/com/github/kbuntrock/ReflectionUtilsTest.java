package com.github.kbuntrock;

import com.github.kbuntrock.resources.dto.Authority;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author Kevin Buntrock
 */
public class ReflectionUtilsTest {

    private List<List<Set<Authority>>> o1;
    private List<Authority> o2;
    private Authority o3;

    @Test
    public void test() throws ClassNotFoundException, NoSuchFieldException {

        Field field = ReflectionUtilsTest.class.getDeclaredField("o1");
        Class<?> myClass = field.getType();
        ParameterizedType parameterizedType = ((ParameterizedType) field.getGenericType());
        Assertions.assertNotNull(parameterizedType);
        Assertions.assertEquals("interface java.util.List", parameterizedType.getRawType().toString());

        Field field2 = ReflectionUtilsTest.class.getDeclaredField("o2");
        Class<?> myClass2 = field2.getType();
        ParameterizedType parameterizedType2 = ((ParameterizedType) field2.getGenericType());
        Assertions.assertNotNull(parameterizedType2);
        Assertions.assertEquals("interface java.util.List", parameterizedType2.getRawType().toString());
        Assertions.assertEquals("class com.github.kbuntrock.resources.dto.Authority", parameterizedType2.getActualTypeArguments()[0].toString());

        Field field3 = ReflectionUtilsTest.class.getDeclaredField("o3");
        Class<?> myClass3 = field3.getType();
        Type parameterizedType3 = field3.getGenericType();
        Assertions.assertEquals("class com.github.kbuntrock.resources.dto.Authority", parameterizedType3.toString());
    }
}
