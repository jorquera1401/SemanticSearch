package org.mjj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.BookSearchController;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
@ComponentScan(basePackages = {"org.mjj", "controller"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    List<Book> loadBooks() {
        ClassPathResource resource = new ClassPathResource("data.json");

        try {
            InputStream inputStream =  resource.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Book> books = objectMapper.readValue(inputStream, new TypeReference<>() {});

            return books;
        } catch (IOException e) {
            System.err.println("❌ Error al cargar los libros: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @Bean
    CommandLineRunner loadBooks(VectorStore vectorStore) {
        return args -> {
            List<Document> documents = loadBooks().stream()
                    .map(book -> new Document(book.toString()))
                    .toList();

            vectorStore.add(documents);
            System.out.println("✅ " + documents.size() + " libros cargados en el vector store");
        };
    }
}