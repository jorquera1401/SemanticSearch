package controller;

import dto.BookDTO;
import dto.SaludoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.document.Document;

import java.util.List;
@RestController
@RequestMapping("/insert/books")
public class BookInsertController {
    private static final Logger log = LoggerFactory.getLogger(BookInsertController.class);
    final List<BookDTO> books;
    final VectorStore store;

    public BookInsertController(VectorStore vectorStore, List<BookDTO> books) {
        this.store = vectorStore;
        this.books = books;
    }

    @PostMapping("/new")
    public void insert(@RequestBody BookDTO book) {
        List<BookDTO> newBooks = this.addNew(book);
        if (newBooks == null) {
            return;
        }
        List<Document> documents = newBooks.stream().map(b -> new Document(b.toJSON())).toList();
        this.store.add(documents);
        this.books.addAll(newBooks);
        log.info("✅ " + newBooks.size() + " libros insertados");
    }

    public List<BookDTO> addNew(BookDTO book){
        if(this.books.contains(book)){
            return null;
        }
        List<BookDTO> newBooks = List.of(book);
        return newBooks;
    }






}
