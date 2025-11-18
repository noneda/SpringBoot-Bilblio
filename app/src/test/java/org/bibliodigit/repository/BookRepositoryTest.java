// package org.bibliodigit.repository;

// import org.bibliodigit.domain.Author;
// import org.bibliodigit.domain.Book;
// import org.bibliodigit.domain.Category;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.test.context.ActiveProfiles;

// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest  
// @ActiveProfiles("test")
// @DisplayName("BookRepository Tests")
// class BookRepositoryTest {

//     @Autowired
//     private TestEntityManager entityManager;

//     @Autowired
//     private BookRepository bookRepository;

//     @Autowired
//     private AuthorRepository authorRepository;

//     @Autowired
//     private CategoryRepository categoryRepository;

//     private Author testAuthor;
//     private Category testCategory;
//     private Book testBook;

//     @BeforeEach
//     void setUp() {
//         bookRepository.deleteAll();
//         authorRepository.deleteAll();
//         categoryRepository.deleteAll();

//         testAuthor = Author.builder()
//             .name("Robert C. Martin")
//             .nationality("USA")
//             .build();
//         testAuthor = authorRepository.save(testAuthor);

//         testCategory = Category.builder()
//             .name("Programming")
//             .description("Software development books")
//             .build();
//         testCategory = categoryRepository.save(testCategory);

//         testBook = Book.builder()
//             .title("Clean Code")
//             .year(2008)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//     }

//     // ========== CRUD BÁSICO ==========

//     @Test
//     @DisplayName("Should save book successfully")
//     void shouldSaveBook() {
//         Book saved = bookRepository.save(testBook);

//         assertThat(saved).isNotNull();
//         assertThat(saved.getId()).isNotNull();
//         assertThat(saved.getTitle()).isEqualTo("Clean Code");
//         assertThat(saved.getYear()).isEqualTo(2008);
//         assertThat(saved.getAuthor().getName()).isEqualTo("Robert C. Martin");
//         assertThat(saved.getCategory().getName()).isEqualTo("Programming");
//     }

//     @Test
//     @DisplayName("Should find all books")
//     void shouldFindAllBooks() {
//         bookRepository.save(testBook);
//         Book anotherBook = Book.builder()
//             .title("The Pragmatic Programmer")
//             .year(1999)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//         bookRepository.save(anotherBook);

//         List<Book> books = bookRepository.findAll();

//         assertThat(books).hasSize(2);
//         assertThat(books).extracting(Book::getTitle)
//             .containsExactlyInAnyOrder("Clean Code", "The Pragmatic Programmer");
//     }

//     @Test
//     @DisplayName("Should find book by id")
//     void shouldFindBookById() {
//         Book saved = bookRepository.save(testBook);

//         Optional<Book> found = bookRepository.findById(saved.getId());

//         assertThat(found).isPresent();
//         assertThat(found.get().getTitle()).isEqualTo("Clean Code");
//     }

//     @Test
//     @DisplayName("Should return empty when book not found")
//     void shouldReturnEmptyWhenBookNotFound() {
//         Optional<Book> found = bookRepository.findById(999L);

//         assertThat(found).isEmpty();
//     }

//     @Test
//     @DisplayName("Should update book")
//     void shouldUpdateBook() {
//         Book saved = bookRepository.save(testBook);
//         saved.setTitle("Clean Code - Updated");
//         saved.setYear(2009);

//         Book updated = bookRepository.save(saved);

//         assertThat(updated.getTitle()).isEqualTo("Clean Code - Updated");
//         assertThat(updated.getYear()).isEqualTo(2009);
//     }

//     @Test
//     @DisplayName("Should delete book")
//     void shouldDeleteBook() {
//         Book saved = bookRepository.save(testBook);

//         bookRepository.deleteById(saved.getId());

//         Optional<Book> found = bookRepository.findById(saved.getId());
//         assertThat(found).isEmpty();
//     }

//     // ========== BÚSQUEDAS POR TÍTULO ==========

//     @Test
//     @DisplayName("Should find books by title containing keyword")
//     void shouldFindBooksByTitleContaining() {
//         bookRepository.save(testBook);
//         Book anotherBook = Book.builder()
//             .title("Clean Architecture")
//             .year(2017)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//         bookRepository.save(anotherBook);

//         List<Book> books = bookRepository.findByTitleContaining("Clean");

//         assertThat(books).hasSize(2);
//         assertThat(books).extracting(Book::getTitle)
//             .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
//     }

//     // ========== BÚSQUEDAS POR AÑO ==========

//     @Test
//     @DisplayName("Should find books by year")
//     void shouldFindBooksByYear() {
//         bookRepository.save(testBook);
//         Book anotherBook = Book.builder()
//             .title("Another Book")
//             .year(2008)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//         bookRepository.save(anotherBook);

//         List<Book> books = bookRepository.findByYear(2008);

//         assertThat(books).hasSize(2);
//     }

//     @Test
//     @DisplayName("Should find books by year range")
//     void shouldFindBooksByYearBetween() {
//         bookRepository.save(testBook);  // 2008
//         Book book2000 = Book.builder()
//             .title("Book 2000")
//             .year(2000)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//         bookRepository.save(book2000);

//         List<Book> books = bookRepository.findByYearBetween(2000, 2010);


//         assertThat(books).hasSize(2);
//         assertThat(books).extracting(Book::getYear)
//             .containsExactlyInAnyOrder(2000, 2008);
//     }

//     // ========== BÚSQUEDAS POR AUTOR ==========

//     @Test
//     @DisplayName("Should find books by author id")
//     void shouldFindBooksByAuthorId() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findByAuthorId(testAuthor.getId());

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getTitle()).isEqualTo("Clean Code");
//     }

//     @Test
//     @DisplayName("Should find books by author")
//     void shouldFindBooksByAuthor() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findByAuthor(testAuthor);

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getAuthor().getName()).isEqualTo("Robert C. Martin");
//     }

//     @Test
//     @DisplayName("Should find books by author name containing")
//     void shouldFindBooksByAuthorNameContaining() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findByAuthorNameContaining("Martin");

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getAuthor().getName()).contains("Martin");
//     }

//     // ========== BÚSQUEDAS POR CATEGORÍA ==========

//     @Test
//     @DisplayName("Should find books by category id")
//     void shouldFindBooksByCategoryId() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findByCategoryId(testCategory.getId());

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getCategory().getName()).isEqualTo("Programming");
//     }

//     @Test
//     @DisplayName("Should find books by category")
//     void shouldFindBooksByCategory() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findByCategory(testCategory);

//         assertThat(books).hasSize(1);
//     }

//     @Test
//     @DisplayName("Should find books by category name")
//     void shouldFindBooksByCategoryName() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findByCategoryName("Programming");

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getCategory().getName()).isEqualTo("Programming");
//     }

//     // ========== BÚSQUEDAS ESPECIALES ==========

//     @Test
//     @DisplayName("Should find books without author")
//     void shouldFindBooksWithoutAuthor() {
//         Book orphanBook = Book.builder()
//             .title("Orphan Book")
//             .year(2020)
//             .category(testCategory)
//             .build();
//         bookRepository.save(orphanBook);

//         List<Book> books = bookRepository.findByAuthorIsNull();

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getTitle()).isEqualTo("Orphan Book");
//     }

//     @Test
//     @DisplayName("Should find books without category")
//     void shouldFindBooksWithoutCategory() {
//         Book uncategorizedBook = Book.builder()
//             .title("Uncategorized Book")
//             .year(2020)
//             .author(testAuthor)
//             .build();
//         bookRepository.save(uncategorizedBook);

//         List<Book> books = bookRepository.findByCategoryIsNull();

//         assertThat(books).hasSize(1);
//         assertThat(books.get(0).getTitle()).isEqualTo("Uncategorized Book");
//     }

//     // ========== CONTEOS ==========

//     @Test
//     @DisplayName("Should count books by author")
//     void shouldCountBooksByAuthor() {
//         bookRepository.save(testBook);
//         Book anotherBook = Book.builder()
//             .title("Another Book")
//             .year(2020)
//             .author(testAuthor)
//             .category(testCategory)
//             .build();
//         bookRepository.save(anotherBook);

//         Long count = bookRepository.countByAuthorId(testAuthor.getId());

//         assertThat(count).isEqualTo(2);
//     }

//     @Test
//     @DisplayName("Should count books by category")
//     void shouldCountBooksByCategory() {
//         bookRepository.save(testBook);

//         Long count = bookRepository.countByCategoryId(testCategory.getId());

//         assertThat(count).isEqualTo(1);
//     }

//     // ========== VERIFICACIONES ==========

//     @Test
//     @DisplayName("Should return true when book exists by title")
//     void shouldReturnTrueWhenBookExistsByTitle() {
//         bookRepository.save(testBook);

//         boolean exists = bookRepository.existsByTitle("Clean Code");

//         assertThat(exists).isTrue();
//     }

//     @Test
//     @DisplayName("Should return false when book does not exist by title")
//     void shouldReturnFalseWhenBookDoesNotExistByTitle() {
//         boolean exists = bookRepository.existsByTitle("Non-existent Book");

//         assertThat(exists).isFalse();
//     }

//     // ========== OPTIMIZACIÓN (EntityGraph) ==========

//     @Test
//     @DisplayName("Should load books with author and category using EntityGraph")
//     void shouldLoadBooksWithRelations() {
//         bookRepository.save(testBook);

//         List<Book> books = bookRepository.findAll();

//         assertThat(books).hasSize(1);
//         Book book = books.get(0);
        
//         assertThat(book.getAuthor()).isNotNull();
//         assertThat(book.getAuthor().getName()).isEqualTo("Robert C. Martin");
//         assertThat(book.getCategory()).isNotNull();
//         assertThat(book.getCategory().getName()).isEqualTo("Programming");
//     }
// }
