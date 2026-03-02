package io.github.kbuntrock.resources.dto.genericity;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Kévin Buntrock
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class EntityDto implements Serializable, Cloneable {

	private static final long serialVersionUID = -1L;

	public static final String CREATION_DATE = "creationDate";

	private Date creationDate;
	private Date changeDate;
	@Pattern(regexp = "toto-regex", message = "not matching toto regex")
	@Size(min = 14, max = 15)
	private String uuid;

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public String getUuid() {
		return uuid;
	}
}
