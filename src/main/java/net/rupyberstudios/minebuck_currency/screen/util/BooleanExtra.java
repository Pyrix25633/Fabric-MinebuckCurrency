package net.rupyberstudios.minebuck_currency.screen.util;

public enum BooleanExtra implements ExtraEnum {
    TRUE, FALSE;

    private static final Position position = new Position(92, 166);

    @Override
    public Position getPosition() {
        return switch(this) {
            case TRUE -> position;
            case FALSE -> new Position(position.getX(), position.getY() + 16);
        };
    }

    @Override
    public BooleanExtra getNext() {
        return switch(this) {
            case TRUE -> FALSE;
            case FALSE -> TRUE;
        };
    }

    public boolean toBoolean() {
        return switch(this) {
            case TRUE -> true;
            case FALSE -> false;
        };
    }
}