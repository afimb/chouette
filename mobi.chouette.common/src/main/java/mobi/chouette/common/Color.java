package mobi.chouette.common;

public interface Color {
	public static final String NORMAL = (char) 27 + "[0;39m";
	public static final String BLACK = (char) 27 + "[1;30m";
	public static final String RED = (char) 27 + "[1;31m";
	public static final String GREEN = (char) 27 + "[1;32m";
	public static final String YELLOW = (char) 27 + "[1;33m";
	public static final String BLUE = (char) 27 + "[1;34m";
	public static final String MAGENTA = (char) 27 + "[1;35m";
	public static final String CYAN = (char) 27 + "[1;36m";
	public static final String LIGHT_GRAY = (char) 27 + "[1;37m";
	public static final String DARK_GRAY = (char) 27 + "[1;90m";
	public static final String LIGHT_RED = (char) 27 + "[1;91m";
	public static final String LIGHT_GREEN = (char) 27 + "[1;92m";
	public static final String LIGHT_YELLOW = (char) 27 + "[1;93m";
	public static final String LIGHT_BLUE = (char) 27 + "[1;94m";
	public static final String LIGHT_MAGENTA = (char) 27 + "[1;95m";
	public static final String LIGHT_CYAN = (char) 27 + "[1;96m";
	public static final String WHITE = (char) 27 + "[1;97m";

	public static final String SUCCESS = GREEN;
	public static final String FAILURE = RED;
	public static final String WARNING = YELLOW;

}
