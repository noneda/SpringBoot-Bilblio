package org.bibliodigit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bibliodigit.api.dto.BookRequest;
import org.bibliodigit.api.dto.BookResponse;
import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.Category;
import org.bibliodigit.repository.AuthorRepository;
import org.bibliodigit.repository.BookRepository;
import org.bibliodigit.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Book Integration Tests")
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Author testAuthor;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();

        testAuthor = Author.builder()
            .name("Robert C. Martin")
            .nationality("USA")
            .build();
        testAuthor = authorRepository.save(testAuthor);

        testCategory = Category.builder()
            .name("Programming")
            .description("Software development books")
            .build();
        testCategory = categoryRepository.save(testCategory);
    }

    // ========== CRUD INTEGRATION TESTS ==========

    @Test
    @DisplayName("Integration: Should create book successfully")
    void createBook_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.year").value(2008))
            .andExpect(jsonPath("$.authorId").value(testAuthor.getId()))
            .andExpect(jsonPath("$.authorName").value("Robert C. Martin"))
            .andExpect(jsonPath("$.categoryId").value(testCategory.getId()))
            .andExpect(jsonPath("$.categoryName").value("Programming"));
    }

    @Test
    @DisplayName("Integration: Should get all books")
    void getAllBooks_Integration_Success() throws Exception {
        // Create multiple books
        BookRequest request1 = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        BookRequest request2 = BookRequest.builder()
            .title("The Pragmatic Programmer")
            .year(1999)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isCreated());

        // Get all books
        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].title", containsInAnyOrder("Clean Code", "The Pragmatic Programmer")));
    }

    @Test
    @DisplayName("Integration: Should get book by id")
    void getBookById_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        String response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        BookResponse createdBook = objectMapper.readValue(response, BookResponse.class);

        mockMvc.perform(get("/api/books/" + createdBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdBook.getId()))
            .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    @DisplayName("Integration: Should update book")
    void updateBook_Integration_Success() throws Exception {
        BookRequest createRequest = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        String createResponse = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        BookResponse createdBook = objectMapper.readValue(createResponse, BookResponse.class);

        BookRequest updateRequest = BookRequest.builder()
            .title("Clean Code - Updated Edition")
            .year(2009)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(put("/api/books/" + createdBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Clean Code - Updated Edition"))
            .andExpect(jsonPath("$.year").value(2009));
    }

    @Test
    @DisplayName("Integration: Should delete book")
    void deleteBook_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        String response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        BookResponse createdBook = objectMapper.readValue(response, BookResponse.class);

        mockMvc.perform(delete("/api/books/" + createdBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/" + createdBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // ========== SEARCH INTEGRATION TESTS ==========

    @Test
    @DisplayName("Integration: Should search books by title")
    void searchByTitle_Integration_Success() throws Exception {
        BookRequest request1 = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        BookRequest request2 = BookRequest.builder()
            .title("Clean Architecture")
            .year(2017)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/search/title")
                .param("keyword", "Clean")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].title", containsInAnyOrder("Clean Code", "Clean Architecture")));
    }

    @Test
    @DisplayName("Integration: Should find books by author id")
    void findBooksByAuthorId_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/author/" + testAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].authorId").value(testAuthor.getId()));
    }

    @Test
    @DisplayName("Integration: Should find books by category id")
    void findBooksByCategoryId_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/category/" + testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].categoryId").value(testCategory.getId()));
    }

    @Test
    @DisplayName("Integration: Should find books by year")
    void findBooksByYear_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/year/2008")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].year").value(2008));
    }

    @Test
    @DisplayName("Integration: Should find books by year range")
    void findBooksByYearRange_Integration_Success() throws Exception {
        BookRequest request1 = BookRequest.builder()
            .title("Book 2000")
            .year(2000)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        BookRequest request2 = BookRequest.builder()
            .title("Book 2005")
            .year(2005)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        BookRequest request3 = BookRequest.builder()
            .title("Book 2015")
            .year(2015)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request3)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/year-range")
                .param("start", "2000")
                .param("end", "2010")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].year", containsInAnyOrder(2000, 2005)));
    }

    // ========== VALIDATION TESTS ==========

    @Test
    @DisplayName("Integration: Should fail when creating book without title")
    void createBook_WithoutTitle_ShouldFail() throws Exception {
        BookRequest request = BookRequest.builder()
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration: Should fail when creating book with invalid year")
    void createBook_WithInvalidYear_ShouldFail() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Test Book")
            .year(999)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration: Should fail when creating book with non-existent author")
    void createBook_WithNonExistentAuthor_ShouldFail() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Test Book")
            .year(2008)
            .authorId(999L)
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    // ========== STATISTICS TESTS ==========

    @Test
    @DisplayName("Integration: Should count books by author")
    void countBooksByAuthor_Integration_Success() throws Exception {
        BookRequest request1 = BookRequest.builder()
            .title("Book 1")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        BookRequest request2 = BookRequest.builder()
            .title("Book 2")
            .year(2009)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/count/author/" + testAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(2));
    }

    @Test
    @DisplayName("Integration: Should check if book exists by title")
    void existsByTitle_Integration_Success() throws Exception {
        BookRequest request = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(testAuthor.getId())
            .categoryId(testCategory.getId())
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books/exists")
                .param("title", "Clean Code")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/books/exists")
                .param("title", "Nonexistent Book")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(false));
    }
}
