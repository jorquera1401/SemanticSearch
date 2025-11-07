package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.BookDTO;
import dto.SaludoDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookSearchController {
    final VectorStore vectorStore;
    final ChatClient chat;

    public BookSearchController(VectorStore vectorStore, ChatClient.Builder chatBuilder){
        this.vectorStore = vectorStore;
        this.chat = chatBuilder.build();
    }

    @PostMapping("/search")
    public List<String> search(@RequestBody String query){
        return vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(3))
                .stream()
                .map(Document::getContent)
                .toList();
    }


    @GetMapping("/search/format")
    public List<BookDTO> searchFormat(@RequestParam String query) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        
        return vectorStore.similaritySearch(SearchRequest.query(query).withTopK(3))
            .stream()
            .map(document -> {
                try {
                    return objectMapper.readValue(document.getContent(), BookDTO.class);
                } catch (JsonProcessingException e) {
                    // Fallback to manual parsing if JSON parsing fails
                    String content = document.getContent();
                    try {
                        // Try to parse as JSON string
                        JsonNode node = objectMapper.readTree(content);
                        return new BookDTO(
                            node.get("title").asText(),
                            node.get("author").asText(),
                            node.get("description").asText()
                        );
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to parse book data: " + content, ex);
                    }
                }
            })
            .toList();
    }

    @PostMapping("/search-advanced")
    String enhancedSearch(@RequestBody String query){
        String context = vectorStore.similaritySearch(SearchRequest
                .query(query)
                .withTopK(3))
                .stream()
                .map(Document::getContent)
                .reduce("", (a,b) -> a+b +"\n");

        return chat.prompt()
                .system(context)
                .user(query)
                .call()
                .content();

    }

    @GetMapping("/saludo")
    public SaludoDTO saludo(@RequestParam String nombre, @RequestParam String apellido){
        return new SaludoDTO(nombre, apellido);
    }
}
