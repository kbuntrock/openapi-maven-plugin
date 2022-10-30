package io.github.kbuntrock.resources.dto;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public interface InterfaceDto<T, D> extends ParentInterfaceDto {

	/**
	 * Comment on isReturned1
	 *
	 * @return a nice boolean 1
	 */
	boolean isReturned1();

	/**
	 * Comment on hasReturned2
	 *
	 * @return a nice boolean 2
	 */
	boolean hasReturned2();

	/**
	 * Comment on getReturned3
	 *
	 * @return a nice boolean 3
	 */
	boolean getReturned3();

	/**
	 * Comment on setReturned4
	 *
	 * @return a nice boolean 4
	 */
	boolean setReturned4();

	/**
	 * Comment on helloReturned5
	 *
	 * @return a nice boolean 5
	 */
	boolean helloReturned5();

	/**
	 * Comment on nextReturned6
	 *
	 * @return a nice boolean 6
	 */
	boolean nextReturned6();

	/**
	 * Comment on getAccount
	 *
	 * @return an account
	 */
	AccountDto getAccount();

	/**
	 * Comment on getGeneric
	 *
	 * @return a generic object
	 */
	T getGeneric();

	/**
	 * Comment on getGenericList
	 *
	 * @return a list of generic objects
	 */
	List<D> getGenericList();
}
