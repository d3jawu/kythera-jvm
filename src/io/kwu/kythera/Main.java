package io.kwu.kythera;

import io.kwu.kythera.parser.Parser;
import io.kwu.kythera.parser.node.StatementNode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            String content = Files.readString(Paths.get("./scratch.ky"));
            Parser parser = new Parser(content);
            List<StatementNode> program = parser.parse();
            for(StatementNode st : program) {
                st.print(0);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void printlnWithIndent(String message, int indent) {
        StringBuilder output = new StringBuilder();
        output.append("\t".repeat(Math.max(0, indent)));

        output.append(message);
        System.out.println(output);
    }
}
