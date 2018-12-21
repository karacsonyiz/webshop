package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.database.BasketsDao;
import com.training360.yellowcode.dbTables.Basket;
import com.training360.yellowcode.dbTables.BasketProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class BasketsService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private BasketsDao basketsDao;

    public BasketsService(BasketsDao basketsDao) {
        this.basketsDao = basketsDao;
    }

    public List<BasketProduct> listProducts(long userId) {
        return basketsDao.listProducts(userId);
    }

    public List<Basket> findBasketByUserIdAndProductId(Basket basket) {
        return basketsDao.findBasketByUserIdAndProductId(basket);
    }

    public Response addToBasket(Basket basket) {
        List<Basket> sameProductInUserBasket = findBasketByUserIdAndProductId(basket);
        if (sameProductInUserBasket.size() == 0) {
            basketsDao.addToBasket(basket);
            LOGGER.info(MessageFormat.format("Product (productId: {0}) added to basket of user (userId: {1})",
                    basket.getUserId(), basket.getProductId()));
            return new Response(true, "Termék hozzáadva a kosárhoz.");
        } else {
            Basket previous = sameProductInUserBasket.get(0);
            previous.setQuantity(previous.getQuantity() + basket.getQuantity());
            basketsDao.deleteFromBasketByProductIdAndUserId(basket.getUserId(), basket.getProductId());
            basketsDao.addToBasket(previous);
            LOGGER.info(MessageFormat.format("Product (productId: {0}) quantity in basket of user (userId: {1}) " +
                    "modified by {2}", previous.getProductId(), previous.getUserId(), basket.getQuantity()));
            return new Response(true, MessageFormat.format("Mennyiség {0}-vel növelve.", basket.getQuantity()));
        }
    }

    public Response deleteFromBasketByUserId(long userId) {
        basketsDao.deleteFromBasketByUserId(userId);
        LOGGER.info(MessageFormat.format("Basket of user (userId: {0}) has been removed", userId));
        return new Response(true, "Kosár ürítve.");
    }

    public Response deleteFromBasketByUserIdAndProductId(long userId, long productId) {
        basketsDao.deleteFromBasketByProductIdAndUserId(userId, productId);
        LOGGER.info(MessageFormat.format("Product (productId: {0}) of user (userId: {1}) has been removed",
                productId, userId));
        return new Response(true, "A termék törölve lett a kosárból.");
    }

    public void increaseBasketQuantityByOne(Basket basket) {
        Basket oldBasket = findBasketByUserIdAndProductId(basket).get(0);
        basketsDao.increaseBasketQuantityByOne(oldBasket);
    }

    public void decreaseBasketQuantityByOne(Basket basket) {
        Basket oldBasket = findBasketByUserIdAndProductId(basket).get(0);
        basketsDao.decreaseBasketQuantityByOne(oldBasket);
    }

    public void setBasketQuantity(Basket basket, long quantity) {
        Basket oldBasket = findBasketByUserIdAndProductId(basket).get(0);
        basketsDao.setBasketQuantity(oldBasket, quantity);
    }
}
