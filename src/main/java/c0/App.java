package c0;

import c0.analyzer.TypeChecker;
import c0.analyzer.ir.IRGenerator;
import c0.lexer.CharIterator;
import c0.lexer.Lexer;
import c0.parser.Parser;

import java.io.*;

public class App {
    public static void main(String[] args) {
        String filename = args[0];
        String []tok = filename.split("\\.");
        if (!tok[1].equals("c0")) {
            System.err.printf("wrong extension: %s\n", tok[1]);
            System.exit(1);
        }
        var outFileName = tok[0].concat(".o0");
        if (args.length == 3) {
            outFileName = args[2];
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(filename);
            out = new FileOutputStream(outFileName);
        } catch (FileNotFoundException e) {
            System.err.println("file not found" + e.getMessage());
            System.exit(1);
        }

        try {
            var charIter = new CharIterator(new InputStreamReader(in));
            var lexer = new Lexer(charIter);
            var parser = new Parser(lexer);
            var ast = parser.parse();
//            var dumper = new Dumper(System.out);
//            ast.accept(dumper);
            var typeChecker = new TypeChecker();
            ast.accept(typeChecker);
            var irGenerator = new IRGenerator(out);
            ast.accept(irGenerator);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }
}
