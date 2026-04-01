package com.example.amazonlite.repository;
import com.example.amazonlite.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, String> {
    List<CartItems> findByCartId(String cartId);
    Optional<CartItems> findByCartIdAndProductId(String cartId, String productId);
    Boolean existsByCartIdAndProductId(String cartId , String productId);
    Void deleteByCartIdAndProductId(String cartId,String productId);
    Void deleteByCartId(String cartId);

}

