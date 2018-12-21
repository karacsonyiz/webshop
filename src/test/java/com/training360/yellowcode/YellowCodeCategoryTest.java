package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.dbTables.Category;
import com.training360.yellowcode.userinterface.CategoryController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = "classpath:/clearcategory.sql")
@WithMockUser(username = "testadmin", roles = "ADMIN")
public class YellowCodeCategoryTest {

    @Autowired
    private CategoryController categoryController;

    @Before
    public void addCategories() {
        categoryController.createCategory(new Category(1, "a", 1L));
        categoryController.createCategory(new Category(2, "b", 2L));
        categoryController.createCategory(new Category(3, "c", 3L));
        categoryController.createCategory(new Category(4, "d", 4L));
    }

    @Test
    public void testCategoryCreatWithAlreadyTakenName() {
        Response response = categoryController.createCategory(new Category("a", 5L));
        assertFalse(response.isValidRequest());
        assertEquals("A megadott nevű kategória már létezik!", response.getMessage());
    }

    @Test
    public void testCategoryCreateOrderedInput() {
        List<Category> categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
        Response response = categoryController.createCategory(new Category(5, "e", 5L));
        assertTrue(response.isValidRequest());
        assertEquals("Létrehozva.", response.getMessage());
        categories = categoryController.listCategorys();
        assertEquals(5, categories.size());
    }

    @Test
    public void testCategoryCreateUnorderedInput() {
        List<Category> categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
        Response response = categoryController.createCategory(new Category(5, "e", 7L));
        assertFalse(response.isValidRequest());
        assertEquals("A megadott sorszám túl nagy.", response.getMessage());
        categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
    }

    @Test
    public void testCategoryCreatePutBetweenTwo() {
        List<Category> categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
        Response response = categoryController.createCategory(new Category(5, "e", 2L));
        assertTrue(response.isValidRequest());
        categories = categoryController.listCategorys();
        assertEquals(5, categories.size());

        assertEquals(categories.get(1).getPositionNumber(), Long.valueOf(2));
        assertEquals(categories.get(1).getId(), 5);
        assertEquals(categories.get(1).getName(), "e");

        assertEquals(categories.get(2).getPositionNumber(), Long.valueOf(3));
        assertEquals(categories.get(2).getId(), 2);
        assertEquals(categories.get(2).getName(), "b");

        assertEquals(categories.get(0).getPositionNumber(), Long.valueOf(1));
        assertEquals(categories.get(0).getId(), 1);
        assertEquals(categories.get(0).getName(), "a");
    }

    @Test
    public void testCategoryCreateWithoutName() {
        List<Category> categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
        Response response = categoryController.createCategory(new Category(5, null, 2L));
        assertFalse(response.isValidRequest());
        assertEquals("A név megadása kötelező.", response.getMessage());
        categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
    }

    @Test
    public void testFindCategoryById() {
        Optional<Category> category = categoryController.findCategoryById(1);

        assertTrue(category.isPresent());
        assertEquals("a", category.get().getName());
    }

    @Test
    public void testFindCategoryByInvalidId() {
        Optional<Category> category = categoryController.findCategoryById(5);

        assertFalse(category.isPresent());
        assertEquals(Optional.empty(), category);
    }


    @Test
    public void testCategoryUpdateWithAlreadyTakenName() {
        Response response = categoryController.updateCategory(new Category(1,"b", 1L));
        assertFalse(response.isValidRequest());
        assertEquals("A megadott nevű kategória már létezik!", response.getMessage());
    }

    @Test
    public void testUpdateCategoryNameAndPositionNumber() {
        Response response = categoryController.updateCategory(new Category(1,"aa", 2L));
        assertTrue(response.isValidRequest());
        assertEquals("Módosítva.", response.getMessage());

        List<Category> allCategory = categoryController.listCategorys();

        assertEquals(allCategory.get(0).getId(), 2);
        assertEquals(allCategory.get(0).getName(), "b");
        assertEquals(allCategory.get(0).getPositionNumber(), Long.valueOf(1));
        assertEquals(allCategory.get(1).getId(), 1);
        assertEquals(allCategory.get(1).getName(), "aa");
        assertEquals(allCategory.get(1).getPositionNumber(), Long.valueOf(2));
        assertEquals(allCategory.get(2).getId(), 3);
        assertEquals(allCategory.get(2).getName(), "c");
        assertEquals(allCategory.get(2).getPositionNumber(), Long.valueOf(3));
        assertEquals(allCategory.get(3).getId(), 4);
        assertEquals(allCategory.get(3).getName(), "d");
        assertEquals(allCategory.get(3).getPositionNumber(), Long.valueOf(4));

    }

    @Test
    public void testCategoryUpdateWithChangedOrder() {
        Response response = categoryController.updateCategory(new Category(1, "a", 4L));
        assertTrue(response.isValidRequest());

        List<Category> allCategory = categoryController.listCategorys();

        assertEquals(allCategory.get(0).getId(), 2);
        assertEquals(allCategory.get(0).getPositionNumber(), Long.valueOf(1));
        assertEquals(allCategory.get(1).getId(), 3);
        assertEquals(allCategory.get(1).getPositionNumber(), Long.valueOf(2));
        assertEquals(allCategory.get(2).getId(), 4);
        assertEquals(allCategory.get(2).getPositionNumber(), Long.valueOf(3));
        assertEquals(allCategory.get(3).getId(), 1);
        assertEquals(allCategory.get(3).getPositionNumber(), Long.valueOf(4));
    }

    @Test
    public void testCategoryUpdateWithNonFittingPosition() {
        Response response = categoryController.updateCategory(new Category(1, "a", 6L));
        assertFalse(response.isValidRequest());
        List<Category> allCategory = categoryController.listCategorys();

        assertEquals(allCategory.get(0).getId(), 1);
        assertEquals(allCategory.get(0).getPositionNumber(), Long.valueOf(1));
        assertEquals(allCategory.get(1).getId(), 2);
        assertEquals(allCategory.get(1).getPositionNumber(), Long.valueOf(2));
        assertEquals(allCategory.get(2).getId(), 3);
        assertEquals(allCategory.get(2).getPositionNumber(), Long.valueOf(3));
        assertEquals(allCategory.get(3).getId(), 4);
        assertEquals(allCategory.get(3).getPositionNumber(), Long.valueOf(4));
    }

    @Test
    public void testCategoryUpdateWithoutName() {
        List<Category> categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
        Response response = categoryController.updateCategory(new Category(5, null, 2L));
        assertFalse(response.isValidRequest());
        categories = categoryController.listCategorys();
        assertEquals(4, categories.size());
    }

    @Test
    public void testCategoryDeleteExistingCategoryFromMiddle() {
        Response response = categoryController.deleteCategory(2);
        assertTrue(response.isValidRequest());
        List<Category> allCategory = categoryController.listCategorys();

        assertEquals(allCategory.get(0).getId(), 1);
        assertEquals(allCategory.get(0).getPositionNumber(), Long.valueOf(1));
        assertEquals(allCategory.get(1).getId(), 3);
        assertEquals(allCategory.get(1).getPositionNumber(), Long.valueOf(2));
        assertEquals(allCategory.get(2).getId(), 4);
        assertEquals(allCategory.get(2).getPositionNumber(), Long.valueOf(3));
    }

    @Test
    public void testCategoryCreateWithNullPosition() {
        Category category = new Category();
        category.setId(5);
        category.setName("Comedy");
        categoryController.createCategory(category);

        List<Category> allCategory = categoryController.listCategorys();

        assertEquals(5, allCategory.get(4).getId());
        assertEquals("Comedy", allCategory.get(4).getName());
        assertEquals(Long.valueOf(5), allCategory.get(4).getPositionNumber());
    }
}
