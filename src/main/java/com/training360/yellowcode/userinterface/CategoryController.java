package com.training360.yellowcode.userinterface;

import com.training360.yellowcode.businesslogic.CategoryService;
import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.database.DuplicateCategoryException;
import com.training360.yellowcode.dbTables.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/api/category/{id}", method = RequestMethod.GET)
    public Optional<Category> findCategoryById(@PathVariable long id) {
        return categoryService.findCategoryById(id);
    }

    @RequestMapping(value = "/api/categories", method = RequestMethod.GET)
    public List<Category> listCategorys() {
        return categoryService.listCategorys();
    }

    @RequestMapping(value = "/api/categories", method = RequestMethod.POST)
    public Response createCategory(@RequestBody Category category) {
        try {
            categoryService.createCategory(category);
            return new Response(true, "Létrehozva.");
        } catch (IllegalArgumentException iae) {
            return new Response (false, "A név megadása kötelező.");
        } catch (IllegalStateException ise) {
            return new Response(false, "A megadott sorszám túl nagy.");
        } catch (DuplicateCategoryException dce) {
            return new Response(false, "A megadott nevű kategória már létezik!");
        }
    }

    @RequestMapping(value = "/api/categories/update", method = RequestMethod.POST)
    public Response updateCategory(@RequestBody Category category) {
        try {
            categoryService.updateCategory(category);
            return new Response(true, "Módosítva.");
        } catch (IllegalArgumentException iae) {
            return new Response(false, "A név megadása kötelező.");
        } catch (IllegalStateException ise) {
            return new Response(false, "A megadott sorszám túl nagy.");
        } catch (DuplicateCategoryException dce) {
            return new Response(false, "A megadott nevű kategória már létezik!");
        }

    }

    @RequestMapping(value = "/api/categories/{id}", method = RequestMethod.DELETE)
    public Response deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return new Response(true, "Törölve.");
    }
}
