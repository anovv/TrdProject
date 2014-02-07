package com.btceclient.trdproject;

public class Regexp {
	public static final String Digits     = "(\\p{Digit}+)";
	public static final String HexDigits  = "(\\p{XDigit}+)";
	
	public static final String Exp        = "[eE][+-]?"+Digits;
	public static final String regexp    =  ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
	             "[+-]?(" + // Optional sign character
	             "NaN|" +           // "NaN" string
	             "Infinity|" +      // "Infinity" string

	             // A decimal floating-point string representing a finite positive
	             // number without a leading sign has at most five basic pieces:
	             // Digits . Digits ExponentPart FloatTypeSuffix
	             // 
	             // Since this method allows integer-only strings as input
	             // in addition to strings of floating-point literals, the
	             // two sub-patterns below are simplifications of the grammar
	             // productions from the Java Language Specification, 2nd 
	             // edition, section 3.10.2.

	             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
	             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

	             // . Digits ExponentPart_opt FloatTypeSuffix_opt
	             "(\\.("+Digits+")("+Exp+")?)|"+

	       		// Hexadecimal strings
	       		"((" +
	       		// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
	       		"(0[xX]" + HexDigits + "(\\.)?)|" +

	        	// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
	        	"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

	        	")[pP][+-]?" + Digits + "))" +
	            "[fFdD]?))" +
	            "[\\x00-\\x20]*");
}
