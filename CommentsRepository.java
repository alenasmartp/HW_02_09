package de.telran.myshop.HW_02_09;

import de.telran.myshop.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
public interface CommentsRepository extends CrudRepository<Comment, Long> {
    Iterable<Comment> findByProductId(Long productId);

    @Transactional
    @Modifying
    void deleteByProductId(long productId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete from comments where product_id=:productId"
    )
    void deleteAllProductComments(long productId);

    @Query(
            "select com from Comment com where com.product in " +
                    " (select c.products from Card c where c.id=:cardId) "
    )
    Iterable<Comment> getAllCardComments(long cardId);

}

