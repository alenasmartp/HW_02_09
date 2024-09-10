package de.telran.myshop.HW_02_09;

import de.telran.myshop.controllers.RestController;
import de.telran.myshop.entity.Comment;
import de.telran.myshop.entity.Product;
import de.telran.myshop.errors.CommentsException;
import de.telran.myshop.repository.CommentsRepository;
import de.telran.myshop.repository.ProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class CommentsController {

    Logger logger = LoggerFactory.getLogger(de.telran.myshop.controllers.CommentsController.class);

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping("/comments")
    public Iterable<Comment> getAllComments() {
        return commentsRepository.findAll();
    }

    @GetMapping("/products/{productId}/comments")
    public Iterable<Comment> getAllCommentsByProductId(
            @PathVariable Long productId
    ) {
        if(!productsRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product " + productId + "not found");
        }
        return commentsRepository.findByProductId(productId);
    }

    @GetMapping("/comments/{commentId}")
    public Comment getCommentById(
            @PathVariable Long commentId
    ) {
        logger.info("getCommentById starting commentId: {}", commentId);

        Comment comment = commentsRepository.findById(commentId).orElse(null);
        if (comment == null) {
            // throw new IllegalArgumentException("No comment with id " + commentId);
            throw new CommentsException(commentId, "No comment with id " + commentId);
        }
        logger.info("getCommentById returning comment with commentId: {}", commentId);
        return comment;
    }

    @PostMapping("/products/{productId}/comments")
    public Comment createComment(
            @PathVariable Long productId,
            @RequestBody Comment commentRequest
    ) {
        Product product = productsRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("No product with id " + productId);
        }
        commentRequest.setProduct(product);
        return commentsRepository.save(commentRequest);
    }

    @DeleteMapping("/comments/{id}")
    ResponseEntity<Comment> deleteCommentById(
            @PathVariable Long id
    ) {
        commentsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Comment with id " + id + " not found")
        );
        commentsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{id}")
    Comment updateComment(
            @PathVariable Long id,
            @RequestBody Comment commentRequest
    ) {
        Comment comment = commentsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Comment with id " + id + " not found")
        );

        comment.setContent(commentRequest.getContent());
        return commentsRepository.save(comment);
    }

    @DeleteMapping("/products/{id}/comments")
    ResponseEntity<Product> deleteProductComments(
            @PathVariable Long id
    ) {

        if (!productsRepository.existsById(id)) {
            throw new IllegalArgumentException("Product with id " + id + " not found");
        }
        commentsRepository.deleteAllProductComments(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/cards/{cardId}/comments")
    Iterable<Comment> getAllCardComments(
            @PathVariable Long cardId
    ) {
        return commentsRepository.getAllCardComments(cardId);
    }

}
