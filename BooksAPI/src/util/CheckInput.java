package util;

public class CheckInput {
	
	public static boolean isRegExp(String s) {
		char [] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if((c[i] == '.' && c[i-1] != '\\' )|| (c[i] == '|') || (c[i] == '*')) {
				return true;
			}
		}
		return false;	
	}
	
	public static boolean isAlphabetic(String s) {
		return s.chars().allMatch(Character::isLetter);
	}
	
}
