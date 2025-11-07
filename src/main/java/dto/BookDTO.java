package dto;

public record BookDTO(String title, String author, String description) {
    @Override
    public String toString() {
        return String.format("Title: %s, Author: %s, Description: %s",
            title, author, description);
    }

    public String toJSON() {
        return String.format("{\"title\":\"%s\",\"author\":\"%s\",\"description\":\"%s\"}",
            title, author, description);
    }
}