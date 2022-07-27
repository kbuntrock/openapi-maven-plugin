package com.github.kbuntrock.resources.endpoint.javadoc.inheritance.three;

import com.github.kbuntrock.resources.endpoint.javadoc.inheritance.GrandParentInterface;
import com.github.kbuntrock.resources.endpoint.javadoc.inheritance.ParentInterface;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Kevin Buntrock
 */
public interface IChildClassThree extends ParentInterface, GrandParentInterface {

    @GetMapping("/age-plus-one")
    String giveMeMyAgePlusOne(int age);
}
