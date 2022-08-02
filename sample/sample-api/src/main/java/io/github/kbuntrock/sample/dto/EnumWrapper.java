package io.github.kbuntrock.sample.dto;

import java.util.List;

public class EnumWrapper {

    Authority authority;

    List<Authority> authorityList;

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public List<Authority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<Authority> authorityList) {
        this.authorityList = authorityList;
    }
}
