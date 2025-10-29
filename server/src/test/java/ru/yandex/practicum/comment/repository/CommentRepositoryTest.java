package ru.yandex.practicum.comment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.comment.entity.Comment;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void shouldSaveComment() {
        // Given
        User author = User.builder()
                .name("Author")
                .email("author@example.com")
                .build();
        User savedAuthor = entityManager.persistAndFlush(author);

        User owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        User savedOwner = entityManager.persistAndFlush(owner);

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(savedOwner.getId())
                .build();
        Item savedItem = entityManager.persistAndFlush(item);

        Comment comment = Comment.builder()
                .withText("Great item!")
                .withItem(savedItem)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();

        // When
        Comment saved = commentRepository.save(comment);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Great item!", saved.getText());
        assertEquals(savedItem.getId(), saved.getItem().getId());
        assertEquals(savedAuthor.getId(), saved.getUser().getId());
        assertNotNull(saved.getCreated());

        // Verify with EntityManager
        Comment found = entityManager.find(Comment.class, saved.getId());
        assertEquals("Great item!", found.getText());
    }

    @Test
    void shouldFindCommentById() {
        // Given
        User author = User.builder().name("Author").email("author@example.com").build();
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        User savedAuthor = entityManager.persistAndFlush(author);
        User savedOwner = entityManager.persistAndFlush(owner);

        Item item = Item.builder()
                .name("Items")
                .description("Description")
                .available(true)
                .ownerId(savedOwner.getId())
                .build();
        Item savedItem = entityManager.persistAndFlush(item);

        Comment comment = Comment.builder()
                .withText("Test comment")
                .withItem(savedItem)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();
        Comment savedComment = entityManager.persistAndFlush(comment);

        // When
        Optional<Comment> found = commentRepository.findById(savedComment.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test comment", found.get().getText());
        assertEquals(savedItem.getId(), found.get().getItem().getId());
        assertEquals(savedAuthor.getId(), found.get().getUser().getId());
    }

    @Test
    void shouldFindAllComments() {
        // Given
        User author = User.builder().name("Author").email("author@example.com").build();
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        User savedAuthor = entityManager.persistAndFlush(author);
        User savedOwner = entityManager.persistAndFlush(owner);

        Item item = Item.builder()
                .name("Items")
                .description("Description")
                .available(true)
                .ownerId(savedOwner.getId())
                .build();
        Item savedItem = entityManager.persistAndFlush(item);

        Comment comment1 = Comment.builder()
                .withText("First comment")
                .withItem(savedItem)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .withText("Second comment")
                .withItem(savedItem)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);

        // When
        List<Comment> comments = commentRepository.findAll();

        // Then
        assertNotNull(comments);
        assertTrue(comments.size() >= 2);
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("First comment")));
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("Second comment")));
    }

    @Test
    void shouldUpdateComment() {
        // Given
        User author = User.builder().name("Author").email("author@example.com").build();
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        User savedAuthor = entityManager.persistAndFlush(author);
        User savedOwner = entityManager.persistAndFlush(owner);

        Item item = Item.builder()
                .name("Items")
                .description("Description")
                .available(true)
                .ownerId(savedOwner.getId())
                .build();
        Item savedItem = entityManager.persistAndFlush(item);

        Comment comment = Comment.builder()
                .withText("Original comment")
                .withItem(savedItem)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();
        Comment savedComment = entityManager.persistAndFlush(comment);

        // When
        savedComment.setText("Updated comment");
        Comment updated = commentRepository.save(savedComment);

        // Then
        assertEquals(savedComment.getId(), updated.getId());
        assertEquals("Updated comment", updated.getText());

        // Verify in database
        Comment fromDb = entityManager.find(Comment.class, savedComment.getId());
        assertEquals("Updated comment", fromDb.getText());
    }

    @Test
    void shouldDeleteComment() {
        // Given
        User author = User.builder().name("Author").email("author@example.com").build();
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        User savedAuthor = entityManager.persistAndFlush(author);
        User savedOwner = entityManager.persistAndFlush(owner);

        Item item = Item.builder()
                .name("Items")
                .description("Description")
                .available(true)
                .ownerId(savedOwner.getId())
                .build();
        Item savedItem = entityManager.persistAndFlush(item);

        Comment comment = Comment.builder()
                .withText("Comment to delete")
                .withItem(savedItem)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();
        Comment savedComment = entityManager.persistAndFlush(comment);

        // When
        commentRepository.deleteById(savedComment.getId());

        // Then
        Comment deleted = entityManager.find(Comment.class, savedComment.getId());
        assertNull(deleted);
    }

    @Test
    void shouldHandleCommentsForDifferentItems() {
        // Given
        User author = User.builder().name("Author").email("author@example.com").build();
        User owner1 = User.builder().name("Owner 1").email("owner1@example.com").build();
        User owner2 = User.builder().name("Owner 2").email("owner2@example.com").build();
        User savedAuthor = entityManager.persistAndFlush(author);
        User savedOwner1 = entityManager.persistAndFlush(owner1);
        User savedOwner2 = entityManager.persistAndFlush(owner2);

        Item item1 = Item.builder().name("Item 1").description("Desc 1").available(true).ownerId(savedOwner1.getId()).build();
        Item item2 = Item.builder().name("Item 2").description("Desc 2").available(true).ownerId(savedOwner2.getId()).build();
        Item savedItem1 = entityManager.persistAndFlush(item1);
        Item savedItem2 = entityManager.persistAndFlush(item2);

        Comment comment1 = Comment.builder()
                .withText("Comment for item 1")
                .withItem(savedItem1)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .withText("Comment for item 2")
                .withItem(savedItem2)
                .withUser(savedAuthor)
                .withCreated(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(comment1);
        entityManager.persistAndFlush(comment2);

        // When
        List<Comment> allComments = commentRepository.findAll();

        // Then
        assertNotNull(allComments);
        assertEquals(2, allComments.size());
        assertTrue(allComments.stream().anyMatch(c -> c.getItem().getId().equals(savedItem1.getId())));
        assertTrue(allComments.stream().anyMatch(c -> c.getItem().getId().equals(savedItem2.getId())));
    }
}