package c0.lexer;

import c0.error.UnreachableException;
import c0.RichIterator;

import java.nio.channels.NonReadableChannelException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lexer extends RichIterator<Token> {
    private final CharIterator charIter;
    private final LinkedHashMap<Predicate<Character>, Supplier<Token>> actions;
    int lookAhead;

    public Lexer(CharIterator charIter) {
        this.charIter = charIter;
        actions = new LinkedHashMap<>(Map.of(
                Character::isWhitespace, this::ignoreSpace,
                this::isUdlineorLetter, this::lexIDorKey,
                Character::isDigit, this::lexIntorDb,
                ch -> ch.equals('"'), this::lexString,
                ch -> ch.equals('\''), this::lexChar,
                this::isOperator, this::lexOperatorOrComment
        ));
    }

    @Override
    public Optional<Token> getNext() {
        if (!charIter.hasNext()) {
            return Optional.of(new Token(TokenType.EOF));
        }
        for (var action : actions.entrySet()) {
            if (charIter.check(action.getKey())) {
                return Optional.of(action.getValue().get());
            }
        }
        throw new RuntimeException("wrong character");
    }

    private Token ignoreSpace() {
        while (charIter.check(Character::isWhitespace)) {
            charIter.next();
        }
        return getNext().orElseThrow(NonReadableChannelException::new);
    }

    private boolean isUdlineorLetter(Character ch) {
        if(Character.isLetter(ch)||ch=='_'){
            return true;
        }
        return false;
    }

    private boolean isUdlineorLetterOrDigit(Character ch) {
        if(Character.isDigit(ch)||isUdlineorLetter(ch)){
            return true;
        }
        return false;
    }

    private Token lexIDorKey() {
        StringBuilder sb = new StringBuilder();
        while (charIter.check(this::isUdlineorLetterOrDigit)) {
            sb.append(charIter.next());
        }
        var value = sb.toString();
        return switch (value) {
            case "let" -> new Token(TokenType.LET);
            case "const" -> new Token(TokenType.CONST);
            case "as" -> new Token(TokenType.AS);
            case "fn" -> new Token(TokenType.FN);
            case "return" -> new Token(TokenType.RETURN);
            case "if" -> new Token(TokenType.IF);
            case "else" -> new Token(TokenType.ELSE);
            case "while" -> new Token(TokenType.WHILE);
            case "continue" -> new Token(TokenType.CONTINUE);
            case "break" -> new Token(TokenType.BREAK);
            default -> new Token(TokenType.IDENT, value);
        };
    }

    private Token lexIntorDb() {
        StringBuilder sb = new StringBuilder();
        while (charIter.check(Character::isDigit)) {
            sb.append(charIter.next());
        }
        if (charIter.test('.')) {
            sb.append('.');
            if (charIter.check(Character::isDigit)) {
                while (charIter.check(Character::isDigit)) {
                    sb.append(charIter.next());
                }
                if (charIter.check(x -> x == 'e' || x == 'E')) {
                    sb.append(charIter.next());
                    if (charIter.check(x -> x == '+' || x == '-')) {
                        sb.append(charIter.next());
                    }
                    if (charIter.check(Character::isDigit)) {
                        while (charIter.check(Character::isDigit)) {
                            sb.append(charIter.next());
                        }
                        return new Token(TokenType.DOUBLE_LITERAL, sb.toString());
                    } else {
                        throw new RuntimeException("wrong double");
                    }
                }
                return new Token(TokenType.DOUBLE_LITERAL, sb.toString());
            } else {
                throw new RuntimeException("wrong double");
            }
        }
        return new Token(TokenType.UINT_LITERAL, sb.toString());
    }

    private Optional<Character> lexExtendChar() {
        if (!charIter.hasNext()) {
            return Optional.empty();
        }
        if (charIter.check('"')) {
            return Optional.empty();
        }
        if (charIter.test('\\')) {
            if (!charIter.hasNext()) {
                throw new RuntimeException("wrong special character");
            }
            else
                return switch (charIter.next()) {
                    case '\\' -> Optional.of('\\');
                    case '"' -> Optional.of('"');
                    case '\'' -> Optional.of('\'');
                    case 'n' -> Optional.of('\n');
                    case 'r' -> Optional.of('\r');
                    case 't' -> Optional.of('\t');
                    default -> throw new RuntimeException("wrong Character");
            };
        }
        return Optional.of(charIter.next());
    }

    private Token lexString() {
        var sb = new StringBuilder();
        charIter.expect('"');
        while (true) {
            var op = lexExtendChar();
            if (op.isEmpty()) {
                break;
            }
            sb.append(op.get());
        }
        charIter.expect('"');
        return new Token(TokenType.STRING_LITERAL, sb.toString());
    }

    private Token lexChar() {
        charIter.expect('\'');
        var op = lexExtendChar();
        var value = op.orElseThrow(() -> new RuntimeException("char literal must not be empty"));
        charIter.expect('\'');
        return new Token(TokenType.CHAR_LITERAL, Integer.valueOf((int) value).toString());
    }

    private boolean isOperator(char ch) {
        return List.of('+', '-', '*', '/', '=', '!', '<', '>', '(', ')', '{', '}', ',', ':', ';').contains(ch);
    }

    private Token lexOperatorOrComment() {
        return switch (charIter.next()) {
            case '+' -> new Token(TokenType.PLUS);
            case '-' -> {
                if (charIter.test('>')) {
                    yield new Token(TokenType.ARROW);
                }
                yield new Token(TokenType.MINUS);
            }
            case '*' -> new Token(TokenType.MUL);
            case '/' -> {
                if (charIter.test('/')) {
                    while (charIter.check(x -> !x.equals('\n'))) {
                        charIter.next();
                    }
                    yield  getNext().orElseThrow(NonReadableChannelException::new);
                }
                yield new Token(TokenType.DIV);
            }
            case '=' -> {
                if (charIter.test('=')) {
                    yield new Token(TokenType.EQ);
                }
                yield new Token(TokenType.ASSIGN);
            }
            case '!' -> {
                charIter.expect('=');
                yield new Token(TokenType.NEQ);
            }
            case '<' -> {
                if (charIter.test('=')) {
                    yield new Token(TokenType.LE);
                }
                yield new Token(TokenType.LT);
            }
            case '>' -> {
                if (charIter.test('=')) {
                    yield new Token(TokenType.GE);
                }
                yield new Token(TokenType.GT);
            }
            case '(' -> new Token(TokenType.L_PAREN);
            case ')' -> new Token(TokenType.R_PAREN);
            case '{' -> new Token(TokenType.L_BRACE);
            case '}' -> new Token(TokenType.R_BRACE);
            case ',' -> new Token(TokenType.COMMA);
            case ':' -> new Token(TokenType.COLON);
            case ';' -> new Token(TokenType.SEMICOLON);
            default -> throw new UnreachableException();
        };
    }

    public int lex(){
        if(lookAhead==-1){
            return 0;
        }
        int i=1;
        while (i<100){
            i++;
        }
        return i;
    }

    public boolean match(int token) {
        if (lookAhead == -1) {
            lookAhead = lex();
        }

        return token == lookAhead;
    }

    public void advance() {
        lookAhead = lex();
    }

    public void runLexer() {
        while (!match(-1)) {
            System.out.println("Token: " + token() + " ,Symbol: " );
            advance();
        }
    }

    private String token() {
        String token = switch (lookAhead) {
            case 0 -> "EOI";
            case 1 -> "PLUS";
            case 2 -> "TIMES";
            case 3 -> "NUM_OR_ID";
            case 4 -> "SEMI";
            case 5 -> "LP";
            case 6 -> "RP";
            default -> "";
        };
        return token;
    }
}
