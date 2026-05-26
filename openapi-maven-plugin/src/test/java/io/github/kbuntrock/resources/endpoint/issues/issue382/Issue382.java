package io.github.kbuntrock.resources.endpoint.issues.issue382;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("api")
public interface Issue382 {

	/**
	 * Get the wrapped error messages
	 *
	 * @return the wrapping object
	 */
	@GetMapping
	ResponseEntity<ExtendedErrorMessageResource> getErrorMessages();

	/**
	 * The message form object
	 */
	class MessageForm {
		private String code;
		private String message;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	/**
	 * The error message resource object
	 */
	class ExtendedErrorMessageResource {

		/**
		 * The list of messages
		 */
		private List<MessageForm> messages;

		public void setMessages(MessageForm messageForm) {
			this.messages = Arrays.asList(messageForm);
		}

		public void setMessages(List<MessageForm> messagesForm) {
			this.messages = messagesForm;
		}

		public List<MessageForm> getMessages() {
			return this.messages;
		}
	}

}
