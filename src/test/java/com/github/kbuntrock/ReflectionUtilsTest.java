package com.github.kbuntrock;

import com.github.kbuntrock.resources.dto.Authority;
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

        Field field2 = ReflectionUtilsTest.class.getDeclaredField("o2");
        Class<?> myClass2 = field2.getType();
        ParameterizedType parameterizedType2 = ((ParameterizedType) field2.getGenericType());

        Field field3 = ReflectionUtilsTest.class.getDeclaredField("o3");
        Class<?> myClass3 = field3.getType();
        Type parameterizedType3 = field3.getGenericType();
        System.out.println("");
    }
}
