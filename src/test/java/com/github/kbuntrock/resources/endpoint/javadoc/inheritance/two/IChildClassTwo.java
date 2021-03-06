package com.github.kbuntrock.resources.endpoint.javadoc.inheritance.two;

import com.github.kbuntrock.resources.Constants;
import com.github.kbuntrock.resources.endpoint.javadoc.inheritance.GrandParentInterface;
import com.github.kbuntrock.resources.endpoint.javadoc.inheritance.ParentInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/child-class-two")
public interface IChildClassTwo extends ParentInterface, GrandParentInterface {

    @GetMapping("/age-plus-one")
    String giveMeMyAgePlusOne(int age);
}
