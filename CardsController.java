package de.telran.myshop.HW_02_09;

import de.telran.myshop.controllers.RestController;
import de.telran.myshop.entity.Card;
import de.telran.myshop.entity.Product;
import de.telran.myshop.repository.CardsRepository;
import de.telran.myshop.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.CardException;
import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CardsController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private CardsRepository cardsRepository;

    @GetMapping("/cards")
    public Iterable<Card> getAllCards() {
        return cardsRepository.findAll();
    }

    @GetMapping("/cards/{id}")
    public Card getCardById(
            @PathVariable Long id
    ) {
        Card card = cardsRepository.findById(id).orElse(null);
        if(card == null) {
            throw new CardException("Card with id " + id + " not found", id);
        }
        return card;
    }

    @GetMapping("/cards/{id}/products")
    public Iterable<Product> getCardProducts(
            @PathVariable Long id
    ) {
        Card card = cardsRepository.findById(id).orElse(null);
        if (card == null) {
            throw new CardException("Card with id " + id + " not found", id);
        }
        return card.getProducts();
    }

    @PostMapping("/products/{productId}/cards")
    public Card addCard(
            @PathVariable Long productId,
            @RequestBody Card cardRequest
    ) {
        Product product = productsRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product with id " + productId + " not found");
        }
        Long cardId = cardRequest.getId();
        if(cardId != null && cardId != 0L) {
            Card existingCard = cardsRepository.findById(cardId).orElseThrow(
                    () -> new de.telran.myshop.HW_02_09.CardException("Card with id " + cardId + " not found", cardId)
            );
            product.addCard(existingCard);
            productsRepository.save(product);
            return existingCard;
        }
        product.addCard(cardRequest);
        return cardsRepository.save(cardRequest);
    }

    @PutMapping("/cards/{id}")
    public Card updateCard(
            @PathVariable Long id,
            @RequestBody Card cardRequest
    ) {

        Card card = cardsRepository.findById(id).orElse(null);
        if (card == null) {
            throw new CardException("Card with id " + id + " not found", id);
        }
        card.setName(cardRequest.getName());
        return cardsRepository.save(card);
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Card> deleteCardById(
            @PathVariable Long id
    ) {
        Card card = cardsRepository.findById(id).orElse(null);
        if (card == null) {
            throw new CardException("Card with id " + id + " not found", id);
        }

        List<Product> products = new ArrayList<>(card.getProducts());
        products.forEach(
                p -> p.removeCard(card.getId())
        );

        productsRepository.saveAll(products);
        cardsRepository.delete(card);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/products/{productId}/cards/{cardId}")
    public ResponseEntity<Product> deleteCardFromProduct(
            @PathVariable Long productId,
            @PathVariable Long cardId
    ) {
        Card card = cardsRepository.findById(cardId).orElse(null);
        if (card == null) {
            throw new CardException("Card with id " + cardId + " not found", cardId);
        }
        Product product = productsRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product with id " + productId + " not found");
        }
        product.removeCard(cardId);
        productsRepository.save(product);
        return ResponseEntity.noContent().build();
    }
}
