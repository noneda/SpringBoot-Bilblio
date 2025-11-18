package org.bibliodigit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bibliodigit.api.dto.BookRequest;
import org.bibliodigit.api.dto.BookResponse;
import org.bibliodigit.api.mapper.BookMapper;
import org.bibliodigit.domain.Author;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.Category;
import org.bibliodigit.domain.port.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("BookController Tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookMapper bookMapper;

    private Book sampleBook;
    private BookRequest sampleRequest;
    private BookResponse sampleResponse;
    private Author sampleAuthor;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleAuthor = Author.builder()
            .id(1L)
            .name("Robert C. Martin")
            .nationality("USA")
            .build();

        sampleCategory = Category.builder()
            .id(1L)
            .name("Programming")
            .description("Software development books")
            .build();

        sampleBook = Book.builder()
            .id(1L)
            .title("Clean Code")
            .year(2008)
            .author(sampleAuthor)
            .category(sampleCategory)
            .build();

        sampleRequest = BookRequest.builder()
            .title("Clean Code")
            .year(2008)
            .authorId(1L)
            .categoryId(1L)
            .build();

        sampleResponse = BookResponse.builder()
            .id(1L)
            .title("Clean Code")
            .year(2008)
            .authorId(1L)
            .authorName("Robert C. Martin")
            .authorNationality("USA")
            .categoryId(1L)
            .categoryName("Programming")
            .categoryDescription("Software development books")
            .build();
    }

    // ========== BOOK CREATION TESTS ==========

    @Test
    @DisplayName("POST /api/books - Should create book successfully")
    void createBook_ShouldReturnCreatedBook() throws Exception {
        when(bookService.create(anyString(), anyInt(), anyLong(), anyLong()))
            .thenReturn(sampleBook);
        when(bookMapper.toResponse(any(Book.class)))
            .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.year").value(2008))
            .andExpect(jsonPath("$.authorId").value(1))
            .andExpect(jsonPath("$.authorName").value("Robert C. Martin"))
            .andExpect(jsonPath("$.categoryId").value(1));

        verify(bookService, times(1)).create("Clean Code", 2008, 1L, 1L);
        verify(bookMapper, times(1)).toResponse(sampleBook);
    }

    @Test
    @DisplayName("POST /api/books - Should return 400 with invalid request (missing title)")
    void createBook_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        BookRequest invalidRequest = BookRequest.builder()
            .year(2008)
            .authorId(1L)
            .categoryId(1L)
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verify(bookService, never()).create(anyString(), anyInt(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("POST /api/books - Should return 400 with invalid year (too small)")
    void createBook_WithInvalidYear_ShouldReturnBadRequest() throws Exception {
        BookRequest invalidRequest = BookRequest.builder()
            .title("Test Book")
            .year(999)  // Invalid: less than 1000
            .authorId(1L)
            .categoryId(1L)
            .build();

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verify(bookService, never()).create(anyString(), anyInt(), anyLong(), anyLong());
    }

    // ========== GET ALL BOOKS TESTS ==========

    @Test
    @DisplayName("GET /api/books - Should return all books")
    void getAllBooks_ShouldReturnListOfBooks() throws Exception {
        Book book2 = Book.builder()
            .id(2L)
            .title("The Pragmatic Programmer")
            .year(1999)
            .author(sampleAuthor)
            .category(sampleCategory)
            .build();

        BookResponse response2 = BookResponse.builder()
            .id(2L)
            .title("The Pragmatic Programmer")
            .year(1999)
            .authorId(1L)
            .authorName("Robert C. Martin")
            .categoryId(1L)
            .categoryName("Programming")
            .build();

        when(bookService.findAll()).thenReturn(Arrays.asList(sampleBook, book2));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);
        when(bookMapper.toResponse(book2)).thenReturn(response2);

        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title").value("Clean Code"))
            .andExpect(jsonPath("$[1].title").value("The Pragmatic Programmer"));

        verify(bookService, times(1)).findAll();
        verify(bookMapper, times(2)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("GET /api/books - Should return 204 when no books exist")
    void getAllBooks_WhenEmpty_ShouldReturnNoContent() throws Exception {
        when(bookService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(bookService, times(1)).findAll();
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    // ========== GET BOOK BY ID TESTS ==========

    @Test
    @DisplayName("GET /api/books/{id} - Should return book by id")
    void getBookById_ShouldReturnBook() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.year").value(2008));

        verify(bookService, times(1)).findById(1L);
        verify(bookMapper, times(1)).toResponse(sampleBook);
    }

    @Test
    @DisplayName("GET /api/books/{id} - Should return 404 when book not found")
    void getBookById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(bookService, times(1)).findById(999L);
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    // ========== UPDATE BOOK TESTS ==========

    @Test
    @DisplayName("PUT /api/books/{id} - Should update book successfully")
    void updateBook_ShouldReturnUpdatedBook() throws Exception {
        Book updatedBook = Book.builder()
            .id(1L)
            .title("Clean Code - Updated")
            .year(2009)
            .author(sampleAuthor)
            .category(sampleCategory)
            .build();

        BookResponse updatedResponse = BookResponse.builder()
            .id(1L)
            .title("Clean Code - Updated")
            .year(2009)
            .authorId(1L)
            .authorName("Robert C. Martin")
            .categoryId(1L)
            .categoryName("Programming")
            .build();

        when(bookMapper.toDomain(any(BookRequest.class))).thenReturn(sampleBook);
        when(bookService.update(eq(1L), any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toResponse(updatedBook)).thenReturn(updatedResponse);

        BookRequest updateRequest = BookRequest.builder()
            .title("Clean Code - Updated")
            .year(2009)
            .authorId(1L)
            .categoryId(1L)
            .build();

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Clean Code - Updated"))
            .andExpect(jsonPath("$.year").value(2009));

        verify(bookMapper, times(1)).toDomain(any(BookRequest.class));
        verify(bookService, times(1)).update(eq(1L), any(Book.class));
        verify(bookMapper, times(1)).toResponse(updatedBook);
    }

    @Test
    @DisplayName("PUT /api/books/{id} - Should return 404 when book not found")
    void updateBook_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(bookMapper.toDomain(any(BookRequest.class))).thenReturn(sampleBook);
        when(bookService.update(eq(999L), any(Book.class)))
            .thenThrow(new RuntimeException("Book not found"));

        mockMvc.perform(put("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
            .andExpect(status().isNotFound());

        verify(bookService, times(1)).update(eq(999L), any(Book.class));
    }

    // ========== DELETE BOOK TESTS ==========

    @Test
    @DisplayName("DELETE /api/books/{id} - Should delete book successfully")
    void deleteBook_ShouldReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteById(1L);

        mockMvc.perform(delete("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - Should return 404 when book not found")
    void deleteBook_WhenNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Book not found"))
            .when(bookService).deleteById(999L);

        mockMvc.perform(delete("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(bookService, times(1)).deleteById(999L);
    }

    // ========== SEARCH BY TITLE TESTS ==========

    @Test
    @DisplayName("GET /api/books/search/title - Should return books matching title")
    void searchByTitle_ShouldReturnMatchingBooks() throws Exception {
        when(bookService.findByTitleContaining("Clean")).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/search/title")
                .param("keyword", "Clean")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Clean Code"));

        verify(bookService, times(1)).findByTitleContaining("Clean");
    }

    @Test
    @DisplayName("GET /api/books/search/title - Should return 204 when no matches")
    void searchByTitle_WhenNoMatches_ShouldReturnNoContent() throws Exception {
        when(bookService.findByTitleContaining("Nonexistent")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books/search/title")
                .param("keyword", "Nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(bookService, times(1)).findByTitleContaining("Nonexistent");
    }

    // ========== SEARCH BY AUTHOR TESTS ==========

    @Test
    @DisplayName("GET /api/books/author/{authorId} - Should return books by author id")
    void getBooksByAuthorId_ShouldReturnBooks() throws Exception {
        when(bookService.findByAuthorId(1L)).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/author/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].authorId").value(1));

        verify(bookService, times(1)).findByAuthorId(1L);
    }

    @Test
    @DisplayName("GET /api/books/search/author - Should return books by author name")
    void searchByAuthorName_ShouldReturnBooks() throws Exception {
        when(bookService.findByAuthorName("Martin")).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/search/author")
                .param("name", "Martin")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].authorName").value("Robert C. Martin"));

        verify(bookService, times(1)).findByAuthorName("Martin");
    }

    // ========== SEARCH BY CATEGORY TESTS ==========

    @Test
    @DisplayName("GET /api/books/category/{categoryId} - Should return books by category id")
    void getBooksByCategoryId_ShouldReturnBooks() throws Exception {
        when(bookService.findByCategoryId(1L)).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/category/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].categoryId").value(1));

        verify(bookService, times(1)).findByCategoryId(1L);
    }

    @Test
    @DisplayName("GET /api/books/search/category - Should return books by category name")
    void searchByCategoryName_ShouldReturnBooks() throws Exception {
        when(bookService.findByCategoryName("Programming")).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/search/category")
                .param("name", "Programming")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].categoryName").value("Programming"));

        verify(bookService, times(1)).findByCategoryName("Programming");
    }

    // ========== SEARCH BY YEAR TESTS ==========

    @Test
    @DisplayName("GET /api/books/year/{year} - Should return books by year")
    void getBooksByYear_ShouldReturnBooks() throws Exception {
        when(bookService.findByYear(2008)).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/year/2008")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].year").value(2008));

        verify(bookService, times(1)).findByYear(2008);
    }

    @Test
    @DisplayName("GET /api/books/year-range - Should return books in year range")
    void getBooksByYearRange_ShouldReturnBooks() throws Exception {
        when(bookService.findByYearBetween(2000, 2010)).thenReturn(Arrays.asList(sampleBook));
        when(bookMapper.toResponse(sampleBook)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/books/year-range")
                .param("start", "2000")
                .param("end", "2010")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].year").value(2008));

        verify(bookService, times(1)).findByYearBetween(2000, 2010);
    }

    // ========== STATISTICS TESTS ==========

    @Test
    @DisplayName("GET /api/books/count/author/{authorId} - Should return book count by author")
    void countBooksByAuthor_ShouldReturnCount() throws Exception {
        when(bookService.countBooksByAuthor(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/books/count/author/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(5));

        verify(bookService, times(1)).countBooksByAuthor(1L);
    }

    @Test
    @DisplayName("GET /api/books/count/category/{categoryId} - Should return book count by category")
    void countBooksByCategory_ShouldReturnCount() throws Exception {
        when(bookService.countBooksByCategory(1L)).thenReturn(10L);

        mockMvc.perform(get("/api/books/count/category/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(10));

        verify(bookService, times(1)).countBooksByCategory(1L);
    }

    @Test
    @DisplayName("GET /api/books/exists - Should return true when book exists")
    void existsByTitle_WhenExists_ShouldReturnTrue() throws Exception {
        when(bookService.existsByTitle("Clean Code")).thenReturn(true);

        mockMvc.perform(get("/api/books/exists")
                .param("title", "Clean Code")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));

        verify(bookService, times(1)).existsByTitle("Clean Code");
    }

    @Test
    @DisplayName("GET /api/books/exists - Should return false when book does not exist")
    void existsByTitle_WhenNotExists_ShouldReturnFalse() throws Exception {
        when(bookService.existsByTitle("Nonexistent Book")).thenReturn(false);

        mockMvc.perform(get("/api/books/exists")
                .param("title", "Nonexistent Book")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(false));

        verify(bookService, times(1)).existsByTitle("Nonexistent Book");
    }
}
