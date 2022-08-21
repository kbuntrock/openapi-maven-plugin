package io.github.kbuntrock.sample.rest;

import java.util.List;

public abstract class AbstractGenericData<E> {

    public abstract List<E> getDataList();

}
