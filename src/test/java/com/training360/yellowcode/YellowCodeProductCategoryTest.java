package com.training360.yellowcode;

import com.training360.yellowcode.dbTables.Category;
import com.training360.yellowcode.dbTables.Product;
import com.training360.yellowcode.dbTables.ProductStatusType;
import com.training360.yellowcode.userinterface.CategoryController;
import com.training360.yellowcode.userinterface.ProductController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"classpath:/clearcategory.sql", "classpath:/clear.sql"})
@WithMockUser(username = "testadmin", roles = "ADMIN")
public class YellowCodeProductCategoryTest {

    @Autowired
    private ProductController productController;

    @Autowired
    private CategoryController categoryController;

    @Before
    public void addCategories() {
        categoryController.createCategory(new Category(1, "Egyéb", 1L));
        categoryController.createCategory(new Category(2, "Egyes", 2L));
    }

    @Test
    public void testCreateProductWithCategory() {
        List<Category> categories = categoryController.listCategorys();
        Category category = categories.get(1);

        productController.createProduct(new Product(
                1, "Fókamóka", "fokamoka", "North Pole", 1200,
                ProductStatusType.ACTIVE, category));

        List<Product> allProducts = productController.listProducts();

        assertEquals(category.getName(), allProducts.get(0).getCategory().getName());
    }

    @Test
    public void testCreateProductWithoutCategory() {
        List<Category> categories = categoryController.listCategorys();
        Category category = categories.get(0);

        productController.createProduct(new Product(
                2, "Zsiráfmóka", "zsirafmoka", "South Pole", 1300,
                ProductStatusType.ACTIVE, category));

        List<Product> allProducts = productController.listProducts();

        assertEquals("Egyéb", allProducts.get(0).getCategory().getName());
    }


}
