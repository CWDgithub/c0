package c0.lexer;

import lombok.Getter;

@Getter
public class Token {
    TokenType tokenType;
    String value;

    public Token(TokenType tokenType, String value){
        this.tokenType=tokenType;
        this.value=value;
    }

    public Token(TokenType tokenType) {
        this(tokenType, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TokenType tokenType) return this.tokenType == tokenType;
        if (!(o instanceof Token token)) return false;

        return tokenType == token.tokenType;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("("+tokenType+",");
        if(value==null){
            stringBuilder.append("");
        }
        else stringBuilder.append(value);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
