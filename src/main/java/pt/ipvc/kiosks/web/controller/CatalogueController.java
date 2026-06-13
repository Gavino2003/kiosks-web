package pt.ipvc.kiosks.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.web.client.CoreApiClient;
import pt.ipvc.kiosks.web.dto.CategoryDto;
import pt.ipvc.kiosks.web.dto.ProductDto;
import pt.ipvc.kiosks.web.dto.StoreDto;
import pt.ipvc.kiosks.web.model.Cart;
import pt.ipvc.kiosks.web.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/loja/{storeId}")
public class CatalogueController {

    @Autowired private CoreApiClient api;

    @GetMapping
    public String catalogue(@PathVariable Long storeId,
                             @RequestParam(required = false) Long categoria,
                             @RequestParam(required = false) Long added,
                             HttpSession session,
                             Model model) {

        StoreDto store = api.getStore(storeId);
        if (store == null || !Boolean.TRUE.equals(store.active)) return "redirect:/";

        session.setAttribute("currentStoreId", storeId);

        List<StoreDto>    allStores  = api.getActiveStores();
        List<CategoryDto> categories = api.getCategoriesByStore(storeId);
        List<ProductDto>  products   = api.getProducts(storeId, categoria);

        String selectedCategoryName = null;
        if (categoria != null) {
            selectedCategoryName = categories.stream()
                    .filter(c -> c.id.equals(categoria))
                    .map(c -> c.categoryName)
                    .findFirst().orElse(null);
        }

        model.addAttribute("store",                store);
        model.addAttribute("allStores",            allStores);
        model.addAttribute("categories",           categories);
        model.addAttribute("products",             products);
        model.addAttribute("selectedCategory",     categoria);
        model.addAttribute("selectedCategoryName", selectedCategoryName);
        model.addAttribute("cart",                 getCart(session));
        model.addAttribute("addedProductId",       added);
        return "catalogue";
    }

    @PostMapping("/carrinho/adicionar")
    public String addToCart(@PathVariable Long storeId,
                             @RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int qty,
                             @RequestParam(required = false) Long categoria,
                             @RequestParam(required = false) String productName,
                             @RequestParam(required = false) String sku,
                             @RequestParam(required = false) BigDecimal price,
                             HttpSession session) {

        if (productName != null && price != null) {
            Cart cart = getCart(session);
            cart.add(new CartItem(productId, productName, sku, price, qty));
            session.setAttribute("cart", cart);
        }

        String redirect = "redirect:/loja/" + storeId;
        if (categoria != null) redirect += "?categoria=" + categoria;
        redirect += (categoria != null ? "&" : "?") + "added=" + productId;
        return redirect;
    }

    @PostMapping("/carrinho/remover")
    public String removeFromCart(@PathVariable Long storeId,
                                  @RequestParam Long productId,
                                  HttpSession session) {
        getCart(session).remove(productId);
        return "redirect:/loja/" + storeId + "/carrinho";
    }

    @GetMapping("/carrinho")
    public String cart(@PathVariable Long storeId, HttpSession session, Model model) {
        StoreDto store = api.getStore(storeId);
        if (store == null) return "redirect:/";
        model.addAttribute("store",     store);
        model.addAttribute("allStores", api.getActiveStores());
        model.addAttribute("cart",      getCart(session));
        return "cart";
    }

    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) { cart = new Cart(); session.setAttribute("cart", cart); }
        return cart;
    }
}
