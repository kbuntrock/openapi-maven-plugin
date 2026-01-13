package io.github.kbuntrock.sample.dto;

/// All the possible authorities
public enum Authority {
	/// Access to the app
	ACCESS_APP,
	/// Allowed to read user **informations**
	READ_USER,
	/// Allowed to update the user.
	///
	/// Please use carefully
	UPDATE_USER
}
