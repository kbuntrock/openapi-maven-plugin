package com.github.kbuntrock.sample.rest;

import com.github.kbuntrock.sample.dto.Authority;
import com.github.kbuntrock.sample.dto.EnumWrapper;
import com.github.kbuntrock.sample.enpoint.EnumPlaytestController;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnumPlaytestControllerImpl implements EnumPlaytestController {

    @Override
    public EnumWrapper getAuthorityWrapper() {
        EnumWrapper wrapper = new EnumWrapper();
        wrapper.setAuthority(Authority.ACCESS_APP);
        wrapper.setAuthorityList(List.of(Authority.ACCESS_APP, Authority.READ_USER));
        return wrapper;
    }

    @Override
    public List<Authority> getAuthorities() {
        return List.of(Authority.UPDATE_USER, Authority.ACCESS_APP);
    }

    @Override
    public Authority getAuthority() {
        return Authority.READ_USER;
    }

    @Override
    public String setAuthorityWrapper(EnumWrapper enumWrapper) {
        return enumWrapper.getAuthority().toString();
    }

    @Override
    public String setAuthorityList(List<Authority> authorities) {
        return authorities.get(0).toString();
    }

    @Override
    public String setAuthorityAsBody(Authority authority) {
        return authority.toString();
    }

    @Override
    public String setAuthorityAsQueryParam(Authority authority) {
        return authority.toString();
    }

    @Override
    public String setAuthorityAsPathParam(Authority authority) {
        return authority.toString();
    }
}
