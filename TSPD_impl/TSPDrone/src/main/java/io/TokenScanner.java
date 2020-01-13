package io;

import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TokenScanner
{
	private static Pattern COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", Pattern.MULTILINE + Pattern.DOTALL);
	private static Pattern SPACE_PATTERN = Pattern.compile("[\\n\\r\\s]", Pattern.MULTILINE);
	private static Pattern MULTIPLE_SPACE_PATTERN = Pattern.compile("\\s\\s+", Pattern.MULTILINE);
	private static String IDENTIFIER_STRING = "A-Za-z0-9_=\\-\\[\\],\\.";
	private static Pattern IDENTIFIER = Pattern.compile("["+IDENTIFIER_STRING+"]+");

	public static String transform(String in)
	{
		return in.replaceAll("[^"+IDENTIFIER_STRING+"]", "_");
	}
	
	public static String removeComments(CharSequence c)
	{
		String stripped = COMMENT_PATTERN.matcher(c).replaceAll("");
		String reduced = MULTIPLE_SPACE_PATTERN.matcher(stripped).replaceAll(" ");
		reduced = SPACE_PATTERN.matcher(reduced).replaceAll(" ");
		return reduced.trim();
	}
	
	private final Scanner _scanner;
	
	public TokenScanner(String input)
	{
		this(input,true);
	}
	
	public TokenScanner(String input, boolean removeComments)
	{
		if (removeComments)
		{
			_scanner = new Scanner(removeComments(input));
		}
		else
		{
			_scanner = new Scanner(input);
		}
		_scanner.useLocale(Locale.US);
	}
        
	public void close() {
		_scanner.close();
	}
        
	public String nextIdentifier() {
		return _scanner.next(IDENTIFIER);
	}
	
	public double nextDouble() {
		return _scanner.nextDouble();
	}

	public int nextInt() {
		return _scanner.nextInt();
	}

	public String toString()
	{
		return _scanner.toString();
	}	
}
