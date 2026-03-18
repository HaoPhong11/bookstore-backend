package com.example.bookstore_db.dto;
import lombok.Data;

@Data
public class ReviewRequest {
    private String BookId;
    private int rating; // từ 1 đến 5
    private String comment;

    public String getBookId() {
        return BookId;
    }

    public void setBookId(String bookId) {
        BookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
