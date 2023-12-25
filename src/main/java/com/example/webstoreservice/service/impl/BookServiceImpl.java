package com.example.webstoreservice.service.impl;

import com.example.commoncode.exception.BookCoverException;
import com.example.commoncode.exception.BookExportException;
import com.example.webstoreservice.model.BookSpecifications;
import com.example.webstoreservice.model.dto.BookDto;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.enums.SortBy;
import com.example.webstoreservice.model.enums.SortDirection;
import com.example.webstoreservice.model.mapper.BookMapper;
import com.example.webstoreservice.repository.BookRepository;
import com.example.webstoreservice.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Реализация сервиса {@link BookService} для работы с электронными книгами.
 */
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  private final BookMapper bookMapper;

  @Value("${book.covers.upload.path}")
  private String uploadPath;

  /**
   * Получение страницы с книгами с учетом фильтрации и сортировки.
   *
   * @param title         Название книги для фильтрации.
   * @param authorId      Идентификатор автора для фильтрации.
   * @param genreId       Идентификатор жанра для фильтрации.
   * @param minPrice      Минимальная цена книги для фильтрации.
   * @param maxPrice      Максимальная цена книги для фильтрации.
   * @param sortBy        Поле, по которому выполняется сортировка ({@link SortBy#TITLE},
   *                      {@link SortBy#PRICE}, {@link SortBy#PUBLICATION_YEAR}).
   * @param sortDirection Направление сортировки ({@link SortDirection#ASC} или
   *                      {@link SortDirection#DESC}).
   * @param pageable      Объект, представляющий информацию о странице и сортировке.
   * @return Страница {@link Page} с объектами {@link BookDto} с учетом условий фильтрации и
   * сортировки.
   * @throws EntityNotFoundException, если не найдены соответствующие книги.
   */
  @Override
  public Page<BookDto> getBooks(String title, Long authorId, Long genreId, BigDecimal minPrice,
      BigDecimal maxPrice, SortBy sortBy, SortDirection sortDirection, Pageable pageable) {
    Specification<Book> spec = Specification
        .where(BookSpecifications.titleContains(title))
        .and(BookSpecifications.authorIs(authorId))
        .and(BookSpecifications.genreIs(genreId))
        .and(BookSpecifications.priceBetween(minPrice, maxPrice))
        .and(BookSpecifications.notDeleted())
        .and(BookSpecifications.orderBy(sortBy, sortDirection));

    Page<Book> books = bookRepository.findAll(spec, pageable);

    if (books.isEmpty()) {
      throw new EntityNotFoundException("Книги не найдены");
    }

    return books.map(bookMapper::bookToBookDto);
  }

  /**
   * Получение объекта {@link BookDto} по идентификатору книги.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link BookDto}.
   * @throws EntityNotFoundException, если книга не найдена.
   */
  @Override
  public BookDto getBookDtoById(Long bookId) {
    return bookMapper.bookToBookDto(getBookById(bookId));
  }

  /**
   * Получение объекта {@link Book} по идентификатору книги.
   *
   * @param bookId Идентификатор книги.
   * @return Объект {@link Book}.
   * @throws EntityNotFoundException, если книга не найдена.
   */
  @Override
  public Book getBookById(Long bookId) {
    return bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
  }

  /**
   * Удаление книги по идентификатору.
   *
   * @param bookId Идентификатор книги.
   * @throws EntityNotFoundException, если книга не найдена.
   */
  @Override
  @Transactional
  public void deleteBookById(Long bookId) {
    Book book = getBookById(bookId);
    book.setDeleted(true);
    bookRepository.save(book);
  }

  /**
   * Сохранение обложки книги.
   *
   * @param bookId Идентификатор книги.
   * @param file   Файл с обложкой книги.
   * @return Путь к сохраненной обложке.
   * @throws IllegalArgumentException, если файл не передан или пуст.
   * @throws EntityNotFoundException,  если книга не найдена.
   * @throws BookCoverException,       если произошла ошибка при сохранении файла обложки.
   */
  @Override
  @Transactional
  public String saveBookCover(Long bookId, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Файл не передан или пуст");
    }

    Book book = getBookById(bookId);

    String uniqueFilename = String.format("book_%d.%s", bookId,
        FilenameUtils.getExtension(file.getOriginalFilename()));

    String filePath = Paths.get(uploadPath).resolve(uniqueFilename).toString()
        .replace("/", "\\");

    try {
      if (Files.notExists(Paths.get(uploadPath))) {
        Files.createDirectories(Paths.get(uploadPath));
      }
      Files.write(Paths.get(filePath), file.getBytes());
      book.setCoverPath(filePath);
      bookRepository.save(book);

      return filePath;

    } catch (IOException e) {

      throw new BookCoverException("Ошибка при сохранении файла обложки");
    }
  }

  /**
   * Получение обложки книги в виде массива байтов.
   *
   * @param bookId Идентификатор книги.
   * @return Обложка книги в виде массива байтов.
   * @throws EntityNotFoundException, если книга не найдена.
   * @throws BookCoverException,      если произошла ошибка при чтении файла обложки.
   */
  @Override
  public byte[] getBookCover(Long bookId) {
    Book book = getBookById(bookId);

    Path coverPath = Path.of(book.getCoverPath());

    try {
      if (Files.exists(coverPath)) {
        return Files.readAllBytes(coverPath);
      } else {
        throw new FileNotFoundException("Обложка не найдена");
      }
    } catch (IOException e) {
      throw new BookCoverException("Ошибка при чтении файла обложки");
    }
  }

  /**
   * Экспорт списка книг в формате Excel.
   *
   * @return Массив байтов, представляющий Excel-файл с информацией о книгах.
   * @throws BookExportException, если произошла ошибка при выгрузке книг в Excel.
   */
  @Override
  public byte[] exportBooksToExcel() {

    List<BookDto> allBooks = Stream.iterate(0, i -> i + 1)
        .map(pageNumber -> bookRepository.findAll(PageRequest.of(pageNumber, 5)))
        .takeWhile(page -> !page.getContent().isEmpty())
        .flatMap(bookPage -> bookPage.stream().map(bookMapper::bookToBookDto)).toList();

    if (allBooks.isEmpty()) {
      throw new BookExportException("Нет данных для экспорта");
    }

    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Books");

      Row headerRow = sheet.createRow(0);
      String[] columns = {"Title", "Author", "Genre", "Publication Year", "Price", "ISBN",
          "Page Count", "Age Rating", "Cover Path", "Deleted"};
      for (int i = 0; i < columns.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columns[i]);
      }

      int rowNum = 1;
      for (BookDto book : allBooks) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(book.title());
        row.createCell(1).setCellValue(book.author().authorName());
        row.createCell(2).setCellValue(book.genre().genreName());
        row.createCell(3).setCellValue(book.publicationYear());
        row.createCell(4).setCellValue(book.price().doubleValue());
        row.createCell(5).setCellValue(book.ISBN());
        row.createCell(6).setCellValue(book.pageCount());
        row.createCell(7).setCellValue(book.ageRating());
        row.createCell(8).setCellValue(book.coverPath());
        row.createCell(9).setCellValue(book.deleted());
      }
      workbook.write(outputStream);

      return outputStream.toByteArray();

    } catch (IOException e) {
      throw new BookExportException("Ошибка при выгрузке книг в Excel");
    }
  }

  /**
   * Сохранение новой книги.
   *
   * @param bookDto Объект {@link BookDto}, представляющий новую книгу.
   * @return Объект {@link BookDto} сохраненной книги.
   * @throws EntityNotFoundException, если книга с таким ISBN уже существует.
   */
  @Override
  @Transactional
  public BookDto saveBook(BookDto bookDto) {
    return bookMapper.bookToBookDto(bookRepository.findByISBN(bookDto.ISBN())
        .orElseGet(() -> bookRepository.save(bookMapper.bookDtoToBook(bookDto))));
  }

  /**
   * Обновление информации о книге.
   *
   * @param bookDto Объект {@link BookDto}, представляющий обновленную информацию о книге.
   * @return Объект {@link BookDto} обновленной книги.
   * @throws EntityNotFoundException, если книга не найдена.
   */
  @Override
  @Transactional
  public BookDto updateBook(BookDto bookDto) {
    Book existingBook = getBookById(bookDto.bookId());

    bookMapper.updateBookFromDto(bookDto, existingBook);

    return bookMapper.bookToBookDto(existingBook);
  }
}