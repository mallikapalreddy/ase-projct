package com.hackathon.myyoutube.helper;

import android.widget.EditText;
import java.util.regex.Pattern;

public class Validation {

	// Regular Expression
	// you can change the expression based on your need
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_REGEX = "\\d{3}-\\d{7}";
	private static final String DATE_PATTERN = "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";

	// Error Messages
	private static final String REQUIRED_MSG = "is Required";
	private static final String EMAIL_MSG = "is Invalid";
	private static final String PHONE_MSG = "###-#######";

	// call this method when you need to check email validation
	public static boolean isEmailAddress(EditText editText, boolean required) {
		return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
	}

	// call this method when you need to check phone number validation
	public static boolean isPhoneNumber(EditText editText, boolean required) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
	}

	// return true if the input field is valid, based on the parameter passed
	public static boolean isDate(EditText editText) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (!hasText(editText))
			return false;

		// pattern doesn't match so returning false
		if (!Pattern.matches(DATE_PATTERN, text)) {
			editText.setError("Valid format: ##/##/####\nOr use the right icon.");
			return false;
		}
		;

		return true;
	}

	// return true if the input field is valid, based on the parameter passed
	public static boolean isValid(EditText editText, String regex,
			String errMsg, boolean required) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText(editText))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, text)) {
			editText.setError(editText.getHint().toString() + errMsg);
			return false;
		}
		;

		return true;
	}

	// check the input field has any text or not
	// return true if it contains text otherwise false
	public static boolean hasText(EditText editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(editText.getHint().toString() + REQUIRED_MSG);
			return false;
		}

		return true;
	}
}
