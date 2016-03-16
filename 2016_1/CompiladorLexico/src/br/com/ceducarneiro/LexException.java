package br.com.ceducarneiro;

public class LexException extends Throwable {

    private char ch;
    private char expectedCh;
    private int position;

    public LexException(char ch, char expectedCh, int position) {
        setCh(ch);
        setExpectedCh(expectedCh);
        setPosition(position);
    }

    public LexException(char ch, int position) {
        this(ch, (char) 0, position);
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public char getExpectedCh() {
        return expectedCh;
    }

    public void setExpectedCh(char expectedCh) {
        this.expectedCh = expectedCh;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        String str;

        if (expectedCh == 0)
            str = String.format("Symbol '%s' not identified at %d", ch == 0 ? "EOF" : String.valueOf(ch), position);
        else
            str = String.format("Symbol '%s' found when expected was '%c' at %d", ch == 0 ? "EOF" : String.valueOf(ch), expectedCh, position);

        return str;
    }
}
