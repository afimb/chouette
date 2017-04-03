package mobi.chouette.exchange.netexprofile.jaxb;

public class NonXmlCharEscaper {

    private static final char REPLACEMENT_CHAR = ' ';

    public String escape(String original) {
        if (original == null) {
            return null;
        }

        char[] chars = original.toCharArray();
        if (!escape(chars, 0, chars.length)) {
            return original;
        }

        return new String(chars);
    }

    public boolean escape(char[] content, int offset, int length) {
        if (content == null) {
            return false;
        }

        boolean filtered = false;

        for (int i = offset; i < offset + length; i++) {
            if (isFiltered(content[i])) {
                filtered = true;
                content[i] = REPLACEMENT_CHAR;
            }
        }

        return filtered;
    }

    private boolean isFiltered(char c) {
        return !(c == 0x9 || c == 0xA || c == 0xD || (c >= 0x20 && c <= 0xD7FF) || (c >= 0xE000 && c <= 0xFFFD));
    }

}
