// package org.bibliodigit.application.service;

// import org.bibliodigit.domain.Author;
// import org.bibliodigit.domain.Book;
// import org.bibliodigit.domain.Category;
// import org.bibliodigit.repository.AuthorRepository;
// import org.bibliodigit.repository.BookRepository;
// import org.bibliodigit.repository.CategoryRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// @DisplayName("BookServiceImpl Tests")
// class BookServiceImplTest {

//     @Mock
//     private BookRepository bookRepository;

//     @Mock
//     private AuthorRepository authorRepository;

//     @Mock
//     private CategoryRepository categoryRepository;

//     @InjectMocks
//     private BookServiceImpl bookService;

//     private Author testAuthor;
//     private Category testCategory;
//     private Book testBook;

//     @BeforeEach
//     void setUp() {
//         testAuthor = Author.builder()
//             .id(1L)
//             .name("Robert C. Martin")
//             .nationality("USA")
//             .build();

//         testCategory = Category.builder()
//             .id(1L)
//             .name("Programming")
//             .description("Software development books")
//             .build();

//         testBook = Book.builder()
//             .id(1L)
//             .title("Clean Code")
//             .year(2008)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//     }

//     // ========== CRUD BÁSICO ==========

//     @Test
//     @DisplayName("Should find all books")
//     void shouldFindAllBooks() {
//         when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findAll();

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getTitle()).isEqualTo("Clean Code");
//         verify(bookRepository, times(1)).findAll();
//     }

//     @Test
//     @DisplayName("Should return empty list when no books exist")
//     void shouldReturnEmptyListWhenNoBooksExist() {
//         when(bookRepository.findAll()).thenReturn(Collections.emptyList());

//         List<Book> books = bookService.findAll();

//         assertThat(books).isEmpty();
//         verify(bookRepository, times(1)).findAll();
//     }

//     @Test
//     @DisplayName("Should find book by id")
//     void shouldFindBookById() {
//         when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

//         Optional<Book> found = bookService.findById(1L);

//         assertThat(found).isPresent();
//         assertThat(found.get().getTitle()).isEqualTo("Clean Code");
//         verify(bookRepository, times(1)).findById(1L);
//     }

//     @Test
//     @DisplayName("Should return empty when book not found")
//     void shouldReturnEmptyWhenBookNotFound() {
//         when(bookRepository.findById(999L)).thenReturn(Optional.empty());

//         Optional<Book> found = bookService.findById(999L);

//         assertThat(found).isEmpty();
//         verify(bookRepository, times(1)).findById(999L);
//     }

//     @Test
//     @DisplayName("Should save book")
//     void shouldSaveBook() {
//         when(bookRepository.save(any(Book.class))).thenReturn(testBook);

//         Book saved = bookService.save(testBook);

//         assertThat(saved).isNotNull();
//         assertThat(saved.getTitle()).isEqualTo("Clean Code");
//         verify(bookRepository, times(1)).save(testBook);
//     }

//     @Test
//     @DisplayName("Should create book with author and category")
//     void shouldCreateBookWithAuthorAndCategory() {
//         when(bookRepository.existsByTitle("Clean Code")).thenReturn(false);
//         when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
//         when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
//         when(bookRepository.save(any(Book.class))).thenReturn(testBook);

//         Book created = bookService.create("Clean Code", 2008, 1L, 1L);

//         assertThat(created).isNotNull();
//         assertThat(created.getTitle()).isEqualTo("Clean Code");
//         assertThat(created.getAuthor().getName()).isEqualTo("Robert C. Martin");
//         assertThat(created.getCategory().getName()).isEqualTo("Programming");
        
//         verify(bookRepository).existsByTitle("Clean Code");
//         verify(authorRepository).findById(1L);
//         verify(categoryRepository).findById(1L);
//         verify(bookRepository).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when creating book with existing title")
//     void shouldThrowExceptionWhenCreatingBookWithExistingTitle() {
//         when(bookRepository.existsByTitle("Clean Code")).thenReturn(true);

//         assertThatThrownBy(() -> bookService.create("Clean Code", 2008, 1L, 1L))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("already exists");

//         verify(bookRepository).existsByTitle("Clean Code");
//         verify(bookRepository, never()).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when author not found")
//     void shouldThrowExceptionWhenAuthorNotFound() {
//         when(bookRepository.existsByTitle("Clean Code")).thenReturn(false);
//         when(authorRepository.findById(999L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> bookService.create("Clean Code", 2008, 999L, 1L))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("Author not found");

//         verify(authorRepository).findById(999L);
//         verify(bookRepository, never()).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when category not found")
//     void shouldThrowExceptionWhenCategoryNotFound() {
//         when(bookRepository.existsByTitle("Clean Code")).thenReturn(false);
//         when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
//         when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> bookService.create("Clean Code", 2008, 1L, 999L))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("Category not found");

//         verify(categoryRepository).findById(999L);
//         verify(bookRepository, never()).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should update book")
//     void shouldUpdateBook() {
//         Book updatedData = Book.builder()
//             .title("Clean Code - Updated")
//             .year(2009)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();

//         when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
//         when(bookRepository.save(any(Book.class))).thenReturn(testBook);

//         Book updated = bookService.update(1L, updatedData);

//         assertThat(updated).isNotNull();
//         verify(bookRepository).findById(1L);
//         verify(bookRepository).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should update book with new author")
//     void shouldUpdateBookWithNewAuthor() {
//         Author newAuthor = Author.builder()
//             .id(2L)
//             .name("Martin Fowler")
//             .nationality("UK")
//             .build();

//         Book updatedData = Book.builder()
//             .title("Clean Code")
//             .year(2008)
//             .author(newAuthor)
//             .category(testCategory)
//             .build();

//         when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
//         when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
//         when(bookRepository.save(any(Book.class))).thenReturn(testBook);

//         Book updated = bookService.update(1L, updatedData);

//         assertThat(updated).isNotNull();
//         verify(bookRepository).findById(1L);
//         verify(authorRepository).findById(2L);
//         verify(bookRepository).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should update book with new category")
//     void shouldUpdateBookWithNewCategory() {
//         Category newCategory = Category.builder()
//             .id(2L)
//             .name("Software Engineering")
//             .description("SE books")
//             .build();

//         Book updatedData = Book.builder()
//             .title("Clean Code")
//             .year(2008)
//             .author(testAuthor)
//             .category(newCategory)
//             .build();

//         when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
//         when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
//         when(bookRepository.save(any(Book.class))).thenReturn(testBook);

//         Book updated = bookService.update(1L, updatedData);

//         assertThat(updated).isNotNull();
//         verify(bookRepository).findById(1L);
//         verify(categoryRepository).findById(2L);
//         verify(bookRepository).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when updating non-existent book")
//     void shouldThrowExceptionWhenUpdatingNonExistentBook() {
//         when(bookRepository.findById(999L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> bookService.update(999L, testBook))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("not found");

//         verify(bookRepository).findById(999L);
//         verify(bookRepository, never()).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when updating with non-existent author")
//     void shouldThrowExceptionWhenUpdatingWithNonExistentAuthor() {
//         Author nonExistentAuthor = Author.builder()
//             .id(999L)
//             .name("Non Existent")
//             .build();

//         Book updatedData = Book.builder()
//             .title("Clean Code")
//             .year(2008)
//             .author(nonExistentAuthor)
//             .category(testCategory)
//             .build();

//         when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
//         when(authorRepository.findById(999L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> bookService.update(1L, updatedData))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("Author not found");

//         verify(bookRepository).findById(1L);
//         verify(authorRepository).findById(999L);
//         verify(bookRepository, never()).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when updating with non-existent category")
//     void shouldThrowExceptionWhenUpdatingWithNonExistentCategory() {
//         Category nonExistentCategory = Category.builder()
//             .id(999L)
//             .name("Non Existent")
//             .build();

//         Book updatedData = Book.builder()
//             .title("Clean Code")
//             .year(2008)
//             .author(testAuthor)
//             .category(nonExistentCategory)
//             .build();

//         when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
//         when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> bookService.update(1L, updatedData))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("Category not found");

//         verify(bookRepository).findById(1L);
//         verify(categoryRepository).findById(999L);
//         verify(bookRepository, never()).save(any(Book.class));
//     }

//     @Test
//     @DisplayName("Should delete book by id")
//     void shouldDeleteBookById() {
//         when(bookRepository.existsById(1L)).thenReturn(true);
//         doNothing().when(bookRepository).deleteById(1L);

//         bookService.deleteById(1L);

//         verify(bookRepository).existsById(1L);
//         verify(bookRepository).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Should throw exception when deleting non-existent book")
//     void shouldThrowExceptionWhenDeletingNonExistentBook() {
//         when(bookRepository.existsById(999L)).thenReturn(false);

//         assertThatThrownBy(() -> bookService.deleteById(999L))
//             .isInstanceOf(RuntimeException.class)
//             .hasMessageContaining("not found");

//         verify(bookRepository).existsById(999L);
//         verify(bookRepository, never()).deleteById(anyLong());
//     }

//     // ========== BÚSQUEDAS ==========

//     @Test
//     @DisplayName("Should find books by author id")
//     void shouldFindBooksByAuthorId() {
//         when(bookRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByAuthorId(1L);

//         assertThat(books).hasSize(1);
//         verify(bookRepository).findByAuthorId(1L);
//     }

//     @Test
//     @DisplayName("Should return empty list when no books found by author id")
//     void shouldReturnEmptyListWhenNoBooksFoundByAuthorId() {
//         when(bookRepository.findByAuthorId(999L)).thenReturn(Collections.emptyList());

//         List<Book> books = bookService.findByAuthorId(999L);

//         assertThat(books).isEmpty();
//         verify(bookRepository).findByAuthorId(999L);
//     }

//     @Test
//     @DisplayName("Should find books by category id")
//     void shouldFindBooksByCategoryId() {
//         when(bookRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByCategoryId(1L);

//         assertThat(books).hasSize(1);
//         verify(bookRepository).findByCategoryId(1L);
//     }

//     @Test
//     @DisplayName("Should find books by author name")
//     void shouldFindBooksByAuthorName() {
//         when(bookRepository.findByAuthorNameContaining("Martin"))
//             .thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByAuthorName("Martin");

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getAuthor().getName()).contains("Martin");
//         verify(bookRepository).findByAuthorNameContaining("Martin");
//     }

//     @Test
//     @DisplayName("Should find books by title containing")
//     void shouldFindBooksByTitleContaining() {
//         when(bookRepository.findByTitleContaining("Clean"))
//             .thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByTitleContaining("Clean");

//         assertThat(books).hasSize(1);
//         verify(bookRepository).findByTitleContaining("Clean");
//     }

//     @Test
//     @DisplayName("Should return empty list when no books found by title")
//     void shouldReturnEmptyListWhenNoBooksFoundByTitle() {
//         when(bookRepository.findByTitleContaining("Nonexistent"))
//             .thenReturn(Collections.emptyList());

//         List<Book> books = bookService.findByTitleContaining("Nonexistent");

//         assertThat(books).isEmpty();
//         verify(bookRepository).findByTitleContaining("Nonexistent");
//     }

//     @Test
//     @DisplayName("Should find books by year")
//     void shouldFindBooksByYear() {
//         when(bookRepository.findByYear(2008)).thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByYear(2008);

//         assertThat(books).hasSize(1);
//         verify(bookRepository).findByYear(2008);
//     }

//     @Test
//     @DisplayName("Should find books by year between")
//     void shouldFindBooksByYearBetween() {
//         when(bookRepository.findByYearBetween(2000, 2010))
//             .thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByYearBetween(2000, 2010);

//         assertThat(books).hasSize(1);
//         verify(bookRepository).findByYearBetween(2000, 2010);
//     }

//     @Test
//     @DisplayName("Should find books by category name")
//     void shouldFindBooksByCategoryName() {
//         when(bookRepository.findByCategoryName("Programming"))
//             .thenReturn(Arrays.asList(testBook));

//         List<Book> books = bookService.findByCategoryName("Programming");

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getCategory().getName()).isEqualTo("Programming");
//         verify(bookRepository).findByCategoryName("Programming");
//     }

//     @Test
//     @DisplayName("Should find books without author")
//     void shouldFindBooksWithoutAuthor() {
//         Book orphanBook = Book.builder()
//             .id(2L)
//             .title("Orphan Book")
//             .year(2020)
//             .category(testCategory)
//             .build();

//         when(bookRepository.findByAuthorIsNull()).thenReturn(Arrays.asList(orphanBook));

//         List<Book> books = bookService.findBooksWithoutAuthor();

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getAuthor()).isNull();
//         verify(bookRepository).findByAuthorIsNull();
//     }

//     @Test
//     @DisplayName("Should find books without category")
//     void shouldFindBooksWithoutCategory() {
//         Book uncategorizedBook = Book.builder()
//             .id(2L)
//             .title("Uncategorized Book")
//             .year(2020)
//             .author(testAuthor)
//             .build();

//         when(bookRepository.findByCategoryIsNull()).thenReturn(Arrays.asList(uncategorizedBook));

//         List<Book> books = bookService.findBooksWithoutCategory();

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getCategory()).isNull();
//         verify(bookRepository).findByCategoryIsNull();
//     }

//     // ========== CONTEOS ==========

//     @Test
//     @DisplayName("Should count books by author")
//     void shouldCountBooksByAuthor() {
//         when(bookRepository.countByAuthorId(1L)).thenReturn(5L);

//         Long count = bookService.countBooksByAuthor(1L);

//         assertThat(count).isEqualTo(5L);
//         verify(bookRepository).countByAuthorId(1L);
//     }

//     @Test
//     @DisplayName("Should return zero count when no books by author")
//     void shouldReturnZeroCountWhenNoBooksFoundByAuthor() {
//         when(bookRepository.countByAuthorId(999L)).thenReturn(0L);

//         Long count = bookService.countBooksByAuthor(999L);

//         assertThat(count).isEqualTo(0L);
//         verify(bookRepository).countByAuthorId(999L);
//     }

//     @Test
//     @DisplayName("Should count books by category")
//     void shouldCountBooksByCategory() {
//         when(bookRepository.countByCategoryId(1L)).thenReturn(10L);

//         Long count = bookService.countBooksByCategory(1L);

//         assertThat(count).isEqualTo(10L);
//         verify(bookRepository).countByCategoryId(1L);
//     }

//     @Test
//     @DisplayName("Should return zero count when no books by category")
//     void shouldReturnZeroCountWhenNoBooksFoundByCategory() {
//         when(bookRepository.countByCategoryId(999L)).thenReturn(0L);

//         Long count = bookService.countBooksByCategory(999L);

//         assertThat(count).isEqualTo(0L);
//         verify(bookRepository).countByCategoryId(999L);
//     }

//     @Test
//     @DisplayName("Should check if book exists by title")
//     void shouldCheckIfBookExistsByTitle() {
//         when(bookRepository.existsByTitle("Clean Code")).thenReturn(true);

//         boolean exists = bookService.existsByTitle("Clean Code");

//         assertThat(exists).isTrue();
//         verify(bookRepository).existsByTitle("Clean Code");
//     }

//     @Test
//     @DisplayName("Should return false when book does not exist by title")
//     void shouldReturnFalseWhenBookDoesNotExistByTitle() {
//         when(bookRepository.existsByTitle("Nonexistent")).thenReturn(false);

//         boolean exists = bookService.existsByTitle("Nonexistent");

//         assertThat(exists).isFalse();
//         verify(bookRepository).existsByTitle("Nonexistent");
//     }
// }
