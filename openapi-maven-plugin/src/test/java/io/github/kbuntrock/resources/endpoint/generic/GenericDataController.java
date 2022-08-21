package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(Constants.BASE_API + "/generic-test")
public class GenericDataController extends AbstractGenericData<AccountDto> {

    @GetMapping("/datalist")
    @Override
    public List<AccountDto> getDataList() {

        AccountDto user1 = new AccountDto();
        user1.setFirstName("JohnL");
        AccountDto user2 = new AccountDto();
        user2.setFirstName("First name number two");
        return Arrays.asList(user1, user2);
    }
}
