package controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
