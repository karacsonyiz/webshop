package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.database.CategoryDao;
import com.training360.yellowcode.database.DuplicateProductException;
import com.training360.yellowcode.database.FeedbackDao;
import com.training360.yellowcode.database.ProductDao;
import com.training360.yellowcode.dbTables.Feedback;
import com.training360.yellowcode.dbTables.Product;
import com.training360.yellowcode.dbTables.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private ProductDao productDao;
    private FeedbackDao feedbackDao;

    public ProductService(ProductDao productDao, FeedbackDao feedbackDao) {
        this.productDao = productDao;
        this.feedbackDao = feedbackDao;
    }

    public Optional<Product> findProductByAddress(String address, User user) {
        Optional<Product> product = productDao.findProductByAddress(address);
        if(product.isPresent()) {
            product.get().setFeedbacks(feedbackDao.findFeedBacksByProductId(product.get().getId()));
            List<Feedback> feedbackList = product.get().getFeedbacks();
            for (Feedback feedback : feedbackList) {
                if (user != null && feedback.getUser().getId() == user.getId()) {
                    feedback.setCanEditOrDelete(true);
                } else {
                    feedback.setCanEditOrDelete(false);
                }
            }
        }
        return product;
    }

    public List<Product> listProductsByCategory(long categoryId) {
        return productDao.listProductsByCategory(categoryId);
    }

    public List<Product> listProducts() {
        return sortProductsNameIdThenProducer(productDao.listProducts());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createProduct(Product product) {
        product.setAddress(ProductAddressGenerator.generateUserFriendlyAddress(product));
        if (productDao.findProductByAddress(product.getAddress()).isPresent() ||
                productDao.findProductById(product.getId()).isPresent()) {
            throw new DuplicateProductException("A product with this id or address already exists.");

        }
        throwIllegalArgumentExceptionIfPriceIsInvalid(product.getCurrentPrice());
        productDao.createProduct(product);
        LOGGER.info(MessageFormat.format(
                "Product added(id: {0}, name: {1}, address: {2}, producer: {3}, currentPrice: {4})",
                product.getId(),
                product.getName(),
                product.getAddress(),
                product.getProducer(),
                product.getCurrentPrice()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(long id, Product product) {
        product.setAddress(ProductAddressGenerator.generateUserFriendlyAddress(product));

        Optional<Product> productWithTheSameAddress = productDao.findProductByAddress(product.getAddress());
        Optional<Product> productWithTheSameId = productDao.findProductById(product.getId());

        if ((productWithTheSameAddress.isPresent() && productWithTheSameAddress.get().getId() != id) ||
                (productWithTheSameId.isPresent() && productWithTheSameId.get().getId() != id)) {
            throw new DuplicateProductException("A product with this id or address already exists.");

        }
        throwIllegalArgumentExceptionIfPriceIsInvalid(product.getCurrentPrice());
        productDao.updateProduct(id, product);
        LOGGER.info(MessageFormat.format(
                "Product modified to -> id: {0}, name: {1}, address: {2}, producer: {3}, currentPrice: {4}",
                product.getId(),
                product.getName(),
                product.getAddress(),
                product.getProducer(),
                product.getCurrentPrice()));
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(long id) {
        productDao.deleteProduct(id);
        LOGGER.info(MessageFormat.format("Product (productId:{0}) set to inactive", id));

    }

    private List<Product> sortProductsNameIdThenProducer(List<Product> products) {
        Collator hungarianLocale = Collator.getInstance(new Locale("hu", "HU"));
        return products.stream()
                .sorted(Comparator.comparing(Product::getName,
                        Comparator.comparing(String::toLowerCase,
                                Comparator.nullsFirst(hungarianLocale)))
                        .thenComparing(Product::getProducer,
                                Comparator.comparing(String::toLowerCase,
                                        Comparator.nullsFirst(hungarianLocale))))
                .collect(Collectors.toList());
    }

    private void throwIllegalArgumentExceptionIfPriceIsInvalid(long price) {
        if (price > 2_000_000 || price <= 0) {
            throw new IllegalArgumentException("Invalid price" + price);
        }
    }

    public List<Product> showLastThreeSoldProducts() {
        return productDao.showLastThreeSoldProducts();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void uploadPicture(byte[] bytes, long productID) {
        productDao.uploadPicture(bytes, productID);
    }
}
