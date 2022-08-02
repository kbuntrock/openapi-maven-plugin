package io.github.kbuntrock.resources.endpoint.javadoc.inheritance;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.PageDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A beautiful description of ChildClassOne
 *
 * @author Kevin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/child-class-one")
public class ChildClassOne extends ParentAbstract implements ParentInterface {

    @Override
    @PostMapping("/pretty-print")
    public String prettyPrint(@RequestParam long number) {
        return null;
    }

    @Override
    @GetMapping("/can-pretty-print")
    public boolean canPrettyPrint() {
        return true;
    }

    /**
     * This documentation should not be read
     *
     * @inheritDoc
     */
    @Override
    @GetMapping("/can-encapsulate")
    public boolean canEncapsulate() {
        return true;
    }

    /**
     * Return the name of this controller
     *
     * @return the name
     */
    @GetMapping("/name")
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Supported functionalities, as a page
     *
     * @return the supported functionalities
     */
    @GetMapping("/functionalities")
    public PageDto<String> getPageFunctionalities() {
        return null;
    }


}
