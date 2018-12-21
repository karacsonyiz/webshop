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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"classpath:/clearcategory.sql", "classpath:/clear.sql"})
@WithMockUser(username = "testadmin", roles = "ADMIN")
public class YellowCodeCategoryFilterTest {


    @Autowired
    private ProductController productController;

    @Autowired
    private CategoryController categoryController;

    @Before
    public void init() {
        categoryController.createCategory(new Category(1, "a", 1L));
        categoryController.createCategory(new Category(2, "b", 2L));
        categoryController.createCategory(new Category(3, "c", 3L));
        categoryController.createCategory(new Category(4, "d", 4L));
        productController.createProduct(new Product(1, "Az aliceblue 50 árnyalata", "aliceblue", "E. L. Doe", 9999, ProductStatusType.ACTIVE, new Category(1, "a", 1L)));
        productController.createProduct(new Product(2, "Legendás programozók és megfigyelésük", "legendas", "J. K. Doe", 3999, ProductStatusType.ACTIVE, new Category(1, "a", 1L)));
        productController.createProduct(new Product(3, "Az 50 első Trainer osztály", "osztaly", "Jack Doe", 5999, ProductStatusType.ACTIVE, new Category(2, "b", 2L)));
        productController.createProduct(new Product(4, "Hogyan neveld a junior fejlesztődet", "junior", "Jane Doe", 6499, ProductStatusType.ACTIVE, new Category(3, "c", 3L)));
        productController.createProduct(new Product(5, "Junior most és mindörökké", "mindorokke", "James Doe", 2999, ProductStatusType.ACTIVE, new Category(3, "c", 3L)));
    }

    @Test
    public void testCategoryFilters() {
        assertEquals(5, productController.listProducts().size());
        assertEquals(2, productController.listProductsByCategory(1).size());
        assertEquals(1, productController.listProductsByCategory(2).size());
        assertEquals(2, productController.listProductsByCategory(3).size());
        assertEquals(0, productController.listProductsByCategory(4).size());
    }
}
