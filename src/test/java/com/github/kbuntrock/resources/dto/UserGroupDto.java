package com.github.kbuntrock.resources.dto;

import java.util.List;

public class UserGroupDto {

    private AccountDto leader;

    private List<AccountDto> members;

    private UserGroupDto mainSubgroup;

    private List<UserGroupDto> subgroups;

    public AccountDto getLeader() {
        return leader;
    }

    public void setLeader(AccountDto leader) {
        this.leader = leader;
    }

    public List<AccountDto> getMembers() {
        return members;
    }

    public void setMembers(List<AccountDto> members) {
        this.members = members;
    }

    public UserGroupDto getMainSubgroup() {
        return mainSubgroup;
    }

    public void setMainSubgroup(UserGroupDto mainSubgroup) {
        this.mainSubgroup = mainSubgroup;
    }

    public List<UserGroupDto> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<UserGroupDto> subgroups) {
        this.subgroups = subgroups;
    }
}
