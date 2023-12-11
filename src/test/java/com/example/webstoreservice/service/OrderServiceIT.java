package com.example.webstoreservice.service;

import static com.example.webstoreservice.model.entity.Author.Fields.authorName;
import static com.example.webstoreservice.model.entity.Genre.Fields.genreName;
import static com.example.webstoreservice.model.entity.Order.Fields.books;
import static com.example.webstoreservice.model.entity.Order.Fields.orderDate;
import static com.example.webstoreservice.model.entity.Order.Fields.status;
import static com.example.webstoreservice.model.entity.Order.Fields.userUuid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.example.commoncode.exception.DuplicateException;
import com.example.commoncode.model.enums.Role;
import com.example.webstoreservice.IntegrationTestBase;
import com.example.webstoreservice.model.dto.OrderDto;
import com.example.webstoreservice.model.dto.UserDto;
import com.example.webstoreservice.model.entity.Author;
import com.example.webstoreservice.model.entity.Book;
import com.example.webstoreservice.model.entity.Genre;
import com.example.webstoreservice.model.entity.Order;
import com.example.webstoreservice.model.enums.OrderStatus;
import com.example.webstoreservice.repository.AuthorRepository;
import com.example.webstoreservice.repository.BookRepository;
import com.example.webstoreservice.repository.GenreRepository;
import com.example.webstoreservice.repository.OrderRepository;
import com.example.webstoreservice.service.impl.OrderServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@WithMockUser(username = "testUser", authorities = "USER")
class OrderServiceIT extends IntegrationTestBase {

  private final OrderServiceImpl orderService;

  private final OrderRepository orderRepository;

  private final BookRepository bookRepository;

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  @MockBean
  private final UserService userService;

  private List<Book> testBooks;
  private Order testCart;

  private UserDto testUserDto() {
    return UserDto.builder()
        .userId(1L)
        .userUuid(UUID.randomUUID())
        .username("testUser")
        .email("testEmail")
        .password("testPassword")
        .role(Role.USER)
        .build();
  }

  private List<Book> createTestBooks(String... ISBNs) {
    return Arrays.stream(ISBNs)
        .map(ISBN -> bookRepository.save(Book.builder()
            .title("Title")
            .author(authorRepository.save(
                Author.builder()
                    .authorName(authorName)
                    .build())
            )
            .genre(genreRepository.save(Genre.builder()
                .genreName(genreName)
                .build())
            )
            .ISBN(ISBN)
            .deleted(false)
            .build())).toList();
  }

  private Order createTestCart(UUID uuid, String... ISBNs) {
    List<Book> books = createTestBooks(ISBNs);

    return orderRepository.save(
        Order.builder()
            .userUuid(uuid)
            .status(OrderStatus.IN_CART)
            .books(new HashSet<>(books))
            .build());
  }

  private Order createConfirmedOrder(UUID uuid, LocalDateTime orderDate) {
    return orderRepository.save(
        Order.builder()
            .userUuid(uuid)
            .status(OrderStatus.COMPLETED)
            .books(new HashSet<>(createTestBooks("ISBN")))
            .orderDate(orderDate)
            .build());
  }

  @Test
  void GetCartIfCartExists() {
    UserDto userDto = testUserDto();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    doReturn(userDto).when(userService).getAuthenticatedUser();

    OrderDto orderDto = orderService.getCart();

    assertThat(orderDto).isNotNull();
    assertThat(orderDto.orderDate()).isNull();
    assertThat(orderDto.status()).isEqualTo(testCart.getStatus());
    assertThat(orderDto.orderId()).isEqualTo(testCart.getId());
    assertThat(orderDto.userUuid()).isEqualTo(testCart.getUserUuid());
    assertThat(orderDto.books()).hasSize(1);
    assertThat(orderDto.books())
        .contains(testCart.getBooks().stream().findFirst().orElse(null));
  }

  @Test
  void GetCartIfCartNotExist() {

    doReturn(testUserDto()).when(userService).getAuthenticatedUser();

    OrderDto orderDto = orderService.getCart();

    assertThat(orderDto).isNotNull();
    assertThat(orderDto.orderDate()).isNull();
    assertThat(orderDto.status()).isEqualTo(OrderStatus.IN_CART);
    assertThat(orderDto.orderId()).isNull();
    assertThat(orderDto.books()).isEmpty();
  }

  @Test
  void GetCartInternalIfFound() {
    UserDto userDto = testUserDto();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    doReturn(userDto).when(userService).getAuthenticatedUser();

    Order resultCart = orderService.getCartInternal();

    assertThat(resultCart)
        .isNotNull()
        .isEqualTo(testCart);
  }

  @Test
  void getCartInternalIfFoundButNotInCartStatus() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    orderRepository.save(Order.builder()
        .userUuid(userDto.userUuid())
        .status(OrderStatus.COMPLETED)
        .build());

    assertThatThrownBy(orderService::getCartInternal).isInstanceOf(
        EntityNotFoundException.class).hasMessage("Корзина не найдена");
  }

  @Test
  void GetCartInternalIfCartDoesNotFound() {
    doReturn(testUserDto()).when(userService).getAuthenticatedUser();

    assertThatThrownBy(orderService::getCartInternal).isInstanceOf(
        EntityNotFoundException.class).hasMessage("Корзина не найдена");
  }

  @Test
  void AddToCartIfCartDoesNotExist() {
    testBooks = createTestBooks("ISBN");

    doReturn(testUserDto()).when(userService).getAuthenticatedUser();

    OrderDto orderDto = orderService.addToCart(testBooks.iterator().next().getId());

    Order updatedOrder = orderRepository.findById(orderDto.orderId()).orElse(null);

    assertThat(updatedOrder).isNotNull();
    assertThat(updatedOrder.getOrderDate()).isNull();
    assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.IN_CART);
    assertThat(updatedOrder.getBooks()).hasSize(1);
    assertThat(updatedOrder.getBooks()).contains(testBooks.get(0));
  }

  @Test
  void AddToCartIfBookAlreadyIsInCart() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    assertThatThrownBy(() -> orderService.addToCart(testCart.getBooks().iterator().next().getId()))
        .isInstanceOf(DuplicateException.class)
        .hasMessage("Книга уже добавлена в корзину");
  }

  @Test
  void addToCartIfCartHasOtherBooks() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    OrderDto orderDto = orderService.addToCart(createTestBooks("otherISBN").iterator().next()
        .getId());

    assertThat(orderDto).isNotNull();
    assertThat(orderDto.books()).hasSize(2);
    assertThat(orderDto.books()).containsExactlyInAnyOrderElementsOf(testCart.getBooks());
  }

  @Test
  void removeFromCartIfCartHasOneBook() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    orderService.removeFromCart(testCart.getBooks().iterator().next().getId());

    Order shouldBeNull = orderRepository.findById(testCart.getId()).orElse(null);

    assertThat(shouldBeNull).isNull();
  }

  @Test
  void removeFromCartIfCartHasMoreOneBook() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "ISBN", "otherISBN");

    orderService.removeFromCart(testCart.getBooks().stream()
        .filter(book -> "otherISBN".equals(book.getISBN()))
        .findFirst()
        .map(Book::getId)
        .orElse(null));

    Order updatedOrder = orderRepository.findById(testCart.getId()).orElse(null);

    assertThat(updatedOrder)
        .isNotNull()
        .hasFieldOrPropertyWithValue(userUuid, testCart.getUserUuid())
        .hasFieldOrPropertyWithValue(status, OrderStatus.IN_CART)
        .hasFieldOrPropertyWithValue(books, testCart.getBooks());

    assertThat(updatedOrder.getBooks()).hasSize(1);
    assertThat(updatedOrder.getBooks())
        .extracting(Book::getId)
        .contains(testCart.getBooks().stream()
            .filter(book -> "ISBN".equals(book.getISBN()))
            .map(Book::getId)
            .toArray(Long[]::new));
  }

  @Test
  void removeFromCartIfBookDoesNotFound() {
    testBooks = createTestBooks("ISBN");

    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "otherISBN");

    assertThatThrownBy(() -> orderService.removeFromCart(testBooks.stream()
        .filter(book -> "ISBN".equals(book.getISBN()))
        .findFirst()
        .map(Book::getId)
        .orElse(null)))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Книга не найдена в корзине");

    Order updatedOrder = orderRepository.findById(testCart.getId()).orElse(null);

    assertThat(updatedOrder).isNotNull();
    assertThat(updatedOrder.getBooks()).hasSize(1);
    assertThat(updatedOrder.getBooks())
        .extracting(Book::getId)
        .contains(testCart.getBooks().stream()
            .filter(book -> "otherISBN".equals(book.getISBN()))
            .map(Book::getId)
            .toArray(Long[]::new));
  }

  @Test
  void clearCartIfCartExists() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    orderService.clearCart();

    Order shouldBeNull = orderRepository.findById(testCart.getId()).orElse(null);

    assertThat(shouldBeNull).isNull();
  }

  @Test
  void clearCartIfCartDoesNotExist() {
    doReturn(testUserDto()).when(userService).getAuthenticatedUser();

    assertThatThrownBy(orderService::clearCart)
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Корзина не найдена");
  }

  @Test
  void confirmOrderIfCartExists() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    testCart = createTestCart(userDto.userUuid(), "ISBN");

    Order confirmOrder = orderRepository.findById(orderService.confirmOrder().orderId())
        .orElse(null);

    assertThat(confirmOrder)
        .isNotNull()
        .hasFieldOrPropertyWithValue(userUuid, testCart.getUserUuid())
        .hasFieldOrPropertyWithValue(status, OrderStatus.COMPLETED)
        .hasFieldOrPropertyWithValue(books, testCart.getBooks())
        .hasFieldOrProperty(orderDate);

    assertThat(confirmOrder.getBooks()).hasSize(1);
    assertThat(confirmOrder.getBooks())
        .extracting(Book::getId)
        .contains(testCart.getBooks().stream()
            .filter(book -> "ISBN".equals(book.getISBN()))
            .map(Book::getId)
            .toArray(Long[]::new));

    assertThat(confirmOrder.getOrderDate()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  void confirmOrderIfCartDoesNotExist() {
    doReturn(testUserDto()).when(userService).getAuthenticatedUser();

    assertThatThrownBy(orderService::confirmOrder)
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Корзина не найдена");
  }

  @Test
  void cancelOrderIfOrderExistsAndCanBeCancelled() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    Order confirmedOrder = createConfirmedOrder(userDto.userUuid(),
        LocalDateTime.now().minusDays(1));

    orderService.cancelOrder(confirmedOrder.getId());

    Order checkCancelledOrder = orderRepository.findById(confirmedOrder.getId()).orElse(null);
    assertThat(checkCancelledOrder)
        .isNotNull()
        .hasFieldOrPropertyWithValue(userUuid, confirmedOrder.getUserUuid())
        .hasFieldOrPropertyWithValue(status, OrderStatus.CANCELLED)
        .hasFieldOrPropertyWithValue(books, confirmedOrder.getBooks())
        .hasFieldOrProperty(orderDate);

    assertThat(checkCancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    assertThat(checkCancelledOrder.getOrderDate()).isBeforeOrEqualTo(
        LocalDateTime.now().minusDays(1));
  }

  @Test
  void cancelOrderIfOrderExistsAndCanNotBeCancelled() {
    UserDto userDto = testUserDto();

    doReturn(userDto).when(userService).getAuthenticatedUser();

    Order confirmedOrder = createConfirmedOrder(userDto.userUuid(),
        LocalDateTime.now().minusDays(2));

    assertThatThrownBy(() -> orderService.cancelOrder(confirmedOrder.getId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Срок отмены заказа истек");

    Order checkCancelledOrder = orderRepository.findById(confirmedOrder.getId()).orElse(null);
    assertThat(checkCancelledOrder)
        .isNotNull()
        .hasFieldOrPropertyWithValue(userUuid, confirmedOrder.getUserUuid())
        .hasFieldOrPropertyWithValue(status, OrderStatus.COMPLETED)
        .hasFieldOrPropertyWithValue(books, confirmedOrder.getBooks())
        .hasFieldOrProperty(orderDate);

    assertThat(checkCancelledOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    assertThat(checkCancelledOrder.getOrderDate()).isBeforeOrEqualTo(
        LocalDateTime.now().minusDays(2));
  }

  @Test
  void cancelOrderIfOrderDoesNotExist() {
    doReturn(testUserDto()).when(userService).getAuthenticatedUser();

    assertThatThrownBy(() -> orderService.cancelOrder(1L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Завершенный заказ не найден");
  }

  @AfterEach
  void verifyInteractions() {
    verify(userService).getAuthenticatedUser();
  }
}