package io.github.kbuntrock.resources.endpoint.interfacedto;

import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.InterfaceDto;
import io.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Test a controller returning an interface
 *
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface InterfaceController {

    /**
     * This endpoint returns an interface
     *
     * @return the returned interface
     */
    @GetMapping("interface")
    InterfaceDto<TimeDto, List<Authority>> getInterfaceDto();

}
