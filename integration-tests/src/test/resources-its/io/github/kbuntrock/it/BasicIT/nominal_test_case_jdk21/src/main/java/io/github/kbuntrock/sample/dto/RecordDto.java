package io.github.kbuntrock.sample.dto;

import java.util.List;

/**
 * Dto used for a good reason.
 *
 * @param counter        a counter
 * @param listSubRecords a list of plenty of sub records
 */
public record RecordDto(Integer counter, List<SubRecordDto> listSubRecords) {

	/**
	 * A "classique" record in a record
	 *
	 * @param myBoolean a random boolean
	 */
	public record ClassiqueSubRecordDto(boolean myBoolean) {

	}

	/**
	 * A record in a record
	 *
	 * @param nbSomething number of something
	 * @param code        
	 */
	public record SubRecordDto(Integer nbSomething, String code) {

		/**
		 * A record in a record in a record
		 *
		 * @param nbSomething sub subnumber of something
		 * @param code sub sub code for some reason
		 */
		public record SubSubRecordDto(Integer subSubNbSomething, String subSubCode) {

		}
		
		/**
		 * Custom constructor for SubRecordDto
		 *
		 * @param monCode     monCode descrition
		 */
		public SubRecordDto(final String monCode) {
			this(0, monCode);
		}

		/**
		 * This function should not be scanned
		 *
		 * @return toto
		 */
		public String testFunction() {
			return "toto";
		}

	}

}
