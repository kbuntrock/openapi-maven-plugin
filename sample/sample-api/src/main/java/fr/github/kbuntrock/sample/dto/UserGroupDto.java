package fr.github.kbuntrock.sample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupDto {

    private UserDto leader;

    private List<UserDto> members;

    private UserGroupDto mainSubgroup;

    private List<UserGroupDto> subgroups;

    private Map<Long, UserGroupDto> affiliatedGroups;

    public UserDto getLeader() {
        return leader;
    }

    public void setLeader(UserDto leader) {
        this.leader = leader;
    }

    public List<UserDto> getMembers() {
        return members;
    }

    public void setMembers(List<UserDto> members) {
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

    public Map<Long, UserGroupDto> getAffiliatedGroups() {
        return affiliatedGroups;
    }

    public void setAffiliatedGroups(Map<Long, UserGroupDto> affiliatedGroups) {
        this.affiliatedGroups = affiliatedGroups;
    }
}
