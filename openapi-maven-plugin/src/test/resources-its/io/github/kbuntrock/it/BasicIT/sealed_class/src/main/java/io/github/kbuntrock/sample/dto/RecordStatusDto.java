package io.github.kbuntrock.sample.dto;

/**
 * Represent a service status
 *
 * @param up Indicate if the service is ok (= true) or not ok (= false)
 * @author Kévin Buntrock
 */
public record RecordStatusDto(boolean up) {

}
