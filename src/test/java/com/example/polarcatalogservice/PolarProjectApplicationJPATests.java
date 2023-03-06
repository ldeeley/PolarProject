package com.example.polarcatalogservice;

import com.example.polarcatalogservice.dto.BookRequest;
import com.example.polarcatalogservice.dto.BookResponse;
import com.example.polarcatalogservice.model.Book;
import com.example.polarcatalogservice.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.test.database.replace=NONE",
                "spring.datasource.url=jdbc:tc:mysql:5.7.34:///polarproject"
        }
)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class PolarProjectApplicationJPATests {


    @Autowired
    private WebApplicationContext context;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentationContextProvider){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }


    @Test
    @Sql("/testScripts/init.sql")
    void contextLoads(){
        List<Book> result = bookRepository.findAll();
        assertEquals(12,result.size());
    }

    @Test
    @Sql("/testScripts/init.sql")
    void shouldAddBookToCatalog() throws Exception {

        BookRequest bookRequest = getBookRequest();
        String bookRequestString = objectMapper.writeValueAsString(bookRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookRequestString)).andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
        assertEquals(13, bookRepository.findAll().size());
    }

    private BookRequest getBookRequest() {
        return BookRequest.builder()
                .isbn("978-0-13-516630-7")
                .author("Cay S. Horstmann")
                .title("Core Java Volume 1 Fundamentals")
                .retailPrice(59.99)
                .build();
    }


    @Test
    @Sql("/testScripts/init.sql")
    void postingBookDuplicatePrevented() throws Exception {

        BookRequest bookRequest = getBookRequest();
        String bookRequestString = objectMapper.writeValueAsString(bookRequest);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookRequestString)).andDo(print())
                        .andExpect(status().isCreated())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookRequestString))
                .andExpect(status().isUnprocessableEntity())
                .andDo(document("{methodName}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
        assertEquals(13 , bookRepository.findAll().size());
    }


    @Test
    @Sql("/testScripts/init.sql")
    void getBookForGivenISBN() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("12345")).andDo(document("{methodName}"));

    }

}
