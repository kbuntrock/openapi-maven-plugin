package io.github.kbuntrock.model;

/**
 * Direction of data flow relative to the documented API.
 * Indicates whether an endpoint or operation deals with incoming data only,
 * outgoing data only, or both.
 */
public enum Flow {
	/** Data flows only into the API (e.g. request body, path/query parameters). */
	INPUT,
	/** Data flows only out of the API (e.g. response body). */
	OUTPUT,
	/** Data flows in both directions (e.g. request and response bodies). */
	INPUT_OUTPUT;
}
