package io.github.kbuntrock.resources.dto;

import java.util.List;
import java.util.Map;

public class UserGroupDto {

	private AccountDto leader;

	private List<AccountDto> members;

	private UserGroupDto mainSubgroup;

	private List<UserGroupDto> subgroups;

	private Map<Long, UserGroupDto> affiliatedGroups;

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

	public Map<Long, UserGroupDto> getAffiliatedGroups() {
		return affiliatedGroups;
	}

	public void setAffiliatedGroups(Map<Long, UserGroupDto> affiliatedGroups) {
		this.affiliatedGroups = affiliatedGroups;
	}
}
