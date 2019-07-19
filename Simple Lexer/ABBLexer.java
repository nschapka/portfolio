package roboCompile.ABB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roboCompile.mainProg;
import roboCompile.ABB.DataTypes.Keywords;

public class ABBLexer {

	BufferedReader br;
	List<String> text = new ArrayList<String>();
	final String[] patterns = { "([a-zA-Z]\\w*)(\\{\\d*\\}){0,1}",                // identifiers (one letter followed by alphanumeric characters), possibly an array callout in braces
								"\\-{0,1}\\d*\\.{0,1}\\d+E{0,1}[\\-+]{0,1}\\d*",  // numbers (incl. scientific notation)
								"<>|:=|<=|>=|==|[^\\w\\s\\\"]",    				  // punctuation (anything that is neither alphanumeric nor whitespace)
								"\\\".*?\\\"",                  				  // string literals
								"!.*"											  // comments
								};
	List<Pattern> regexes  = new ArrayList<Pattern>();
	List<Matcher> matchers = new ArrayList<Matcher>();
	Matcher currSymbol = null;
	
	
	public ABBLexer(){
		// open up an input stream
		try {
			br = new BufferedReader( new FileReader("C:\\Users\\nschapka\\Desktop\\code.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// init the regex patterns
		for (String s : patterns) {
			regexes.add(Pattern.compile(s));
		}
		
	}
	
	public void Lex() {
		// pull in the whole file
		readFile();
		
		// iterate every line in the file
		for (String s : text) {	
			// match on the line until there's nothing to match
			while ((currSymbol = nextMatcher(s)) != null) {
				s = currSymbol.replaceFirst("").trim(); // cut out whatever substring was found along with any whitespace between it and the next symbol
				
				// depending on which pattern hit, we need to package the substring in a couple different ways
				switch (currSymbol.pattern().pattern()) {
				case "([a-zA-Z]\\w*)(\\{\\d*\\}){0,1}": // variable names/bool literals/some comparison operators that are words
					mainProg.lexedText.addAll(iden(currSymbol.group(1), currSymbol.group(2)));
					break;
				case "\\-{0,1}\\d*\\.{0,1}\\d+E{0,1}[\\-+]{0,1}\\d*": // numbers
					mainProg.lexedText.add("<NUM> " + currSymbol.group());
					break;
				case "<>|:=|<=|>=|==|[^\\w\\s\\\"]": // punctuation
					mainProg.lexedText.add(punct(currSymbol.group()));
					break;
				case "\\\".*?\\\"": // string literals
					mainProg.lexedText.add("<STRING> " + currSymbol.group());
					break;
				case "!.*": // comments
					mainProg.lexedText.add("<COMMENT> " + currSymbol.group());
				}
			}
			mainProg.lexedText.add("EOL"); // put an EOL between lines
		}
		mainProg.lexedText.add("EOF"); // and an EOF at the end of the whole thing.
	}
	
	private List<String> iden (String s, String arr) {
		List<String> out = new ArrayList<String>();
		
		// first, scan for a match with a keyword from the list,
		for (Keywords key : Keywords.values()) {
			if (key.toString().toUpperCase().equals(s.toUpperCase())) {
				out.add("<KEYWORD> " + s);
			}
		}
		// otherwise, check if it's a boolean literal,
		if (s.toUpperCase().equals("TRUE") || s.toUpperCase().equals("FALSE")) {
			out.add("<BOOL> " + s);
		}
		// a couple other special cases that really should be punctuation but RAPID is a dumb language so half of boolean math is words for no reason
		if (s.toUpperCase().equals("AND")){
			out.add("<AND>");
		} else if (s.toUpperCase().equals("OR")) {
			out.add("<OR>");
		} else if (s.toUpperCase().equals("NOT")) {
			out.add("<NOT>");
		}
		
		// otherwise, it's a variable/function name
		// have to check the size of the list now because we're constructing a list rather than returning a single string
		if (out.size() < 1) {
			out.add("<IDEN> " + s);
		}
		
		// using == instead of the usual .equals method -
		// .equals checks values contained in references, but null isn't an object so that crashes
		// == just checks if the references point to the same object, which for two nulls, technically they do.
		if (arr==null) {
			// do nothing
		} else {
			out.add("<ARRAYSIG> " + arr);
		}
		return out;
	}
	
	private String punct (String s) {
		// dunno of a better way to do this honestly
		switch (s) {
		case "(":
			return "<OPENPAREN>";
		case ")":
			return "<CLOSEPAREN>";
		case "[":
			return "<OPENBRACK>";
		case "]":
			return "<CLOSEBRACK>";
		case "{":
			return "<OPENBRACE>";
		case "}":
			return "<CLOSEBRACE>";
		case ",":
			return "<COMMA>";
		case ".":
			return "<DOT>";
		case "<=":
			return "<LESSEQU>";
		case ">=":
			return "<GRTEQU>";
		case "<":
			return "<LESS>";
		case ">":
			return "<GRT>";
		case "=":
			return "<EQU>";
		case "<>":
			return "<NEQ>";
		case ":=":
			return "<ASSIGN>";
		case ":":
			return "<COLON>";
		case ";":
			return "<SEMICOLON>";
		case "+":
			return "<PLUS>";
		case "-":
			return "<MINUS>";
		case "*":
			return "<MULT>";
		case "/":
			return "<DIV>";
		case "\\":
			return "<BACKSL>";
		default:
			return "";
		}
		
	}
	
	private Matcher nextMatcher(String s) {
		// at a high level, this method returns whichever matcher finds the next lexical token in the input line
		int maxSize = 0;           // init to zero, we want to find sequences bigger than zero
		int matchAt = s.length();  // init to maximum, we want to find a large sequence starting at the smallest index
		Matcher output = null;     // if nothing is found, we return a null Matcher, and count on that to signal the end of a line.
		
		// clear out the list of matchers before we start
		matchers.clear();
		
		// create matcher instances for each pattern, checking against the input string
		for (Pattern p : regexes) {
			matchers.add(p.matcher(s));
		}
		
		// check each matcher, determine which found the longest sequence at the leftmost position in the input string
		for (Matcher m : matchers) {
			if (m.find()) {
				if (    m.start() <  matchAt
					|| (m.start() == matchAt && m.group().length() > maxSize) ) {  // check for leftmost, biggest match
						maxSize = m.group().length();
						matchAt = m.start();
						output = m;
				}
			}
		}
		
		return output;
	}
	
	private void readFile() {
		// uses the buffered reader to read the whole target file into memory (as a list of strings) all at once.
		String tempStr;
		
		try {
			while ((tempStr = br.readLine()) != null) {
				text.add(tempStr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
