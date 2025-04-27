package Helper.Logger;

public class SalamiPrettyLinter {

    public static String format(String input) {
        StringBuilder output = new StringBuilder();
        int indent = 0;
        boolean inString = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Detect string literals
            if (c == '"' || c == '\'') {
                output.append(c);
                char quote = c;
                i++;
                while (i < input.length() && input.charAt(i) != quote) {
                    output.append(input.charAt(i));
                    i++;
                }
                if (i < input.length()) output.append(quote);
                continue;
            }

            switch (c) {
                case '{', '[' -> {
                    output.append(c);
                    output.append("\n");
                    indent++;
                    output.append("    ".repeat(indent));
                }
                case '}', ']' -> {
                    output.append("\n");
                    indent--;
                    output.append("    ".repeat(indent));
                    output.append(c);
                }
                case ',' -> {
                    output.append(c);
                    output.append("\n");
                    output.append("    ".repeat(indent));
                }
                default -> output.append(c);
            }
        }

        return output.toString();
    }
}

