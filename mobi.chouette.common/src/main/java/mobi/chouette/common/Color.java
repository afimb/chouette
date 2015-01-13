package mobi.chouette.common;

public interface Color {
	public static final String BLACK = (char) 27 + "[1;30m";
	public static final String RED = (char) 27 + "[1;31m";
	public static final String GREEN = (char) 27 + "[1;32m";
	public static final String YELLOW = (char) 27 + "[1;33m";
	public static final String BLUE = (char) 27 + "[1;34m";
	public static final String MAGENTA = (char) 27 + "[1;35m";
	public static final String CYAN = (char) 27 + "[1;36m";
	public static final String WHITE = (char) 27 + "[1;37m";
	public static final String NORMAL = (char) 27 + "[0;39m";
	public static final String SUCCESS = GREEN;
	public static final String FAILURE = RED;
	public static final String WARNING = YELLOW;

}
