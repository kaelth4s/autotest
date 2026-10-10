package ru.kaelth4s.object;

public record Game(String name, String released, int price) {
    public Game(String name, String released, int price) {
        this.name = name.trim().toLowerCase();
        this.released = released.trim().toLowerCase();
        this.price = price;
    }

    public boolean matches(Game other) {
        return this.name.equals(other.name) &&
                this.released.equals(other.released) &&
                this.price == other.price;
    }
}
