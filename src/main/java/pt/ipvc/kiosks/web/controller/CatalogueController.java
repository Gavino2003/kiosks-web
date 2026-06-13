package pt.ipvc.kiosks.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.dal.entities.Category;
import pt.ipvc.kiosks.dal.entities.ProductStore;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.repository.CategoryRepository;
import pt.ipvc.kiosks.dal.repository.ProductStoreRepository;
import pt.ipvc.kiosks.dal.repository.StoreRepository;
import pt.ipvc.kiosks.web.model.Cart;
import pt.ipvc.kiosks.web.model.CartItem;

import java.util.List;

@Controller
@RequestMapping("/loja/{storeId}")
public class CatalogueController {

    @Autowired private StoreRepository        storeRepository;
    @Autowired private CategoryRepository     categoryRepository;
    @Autowired private ProductStoreRepository productStoreRepository;

    @GetMapping
    public String catalogue(@PathVariable Long storeId,
                             @RequestParam(required = false) Long categoria,
                             @RequestParam(required = false) String q,
                             @RequestParam(required = false) Long added,
                             HttpSession session,
                             Model model) {

        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null || !store.getActive()) return "redirect:/";

        session.setAttribute("currentStoreId", storeId);

        List<Category> categories = categoryRepository.findByStoreIdStoreAndActiveTrue(storeId);
        List<ProductStore> products = productStoreRepository.findByStoreIdStoreOrderByProductProductName(storeId)
                .stream().filter(ps -> ps.getActive() && ps.getProduct().getActive()).toList();

        // filtro por categoria
        if (categoria != null) {
            final Long fc = categoria;
            products = products.stream()
                    .filter(ps -> ps.getProduct().getCategory() != null
                            && ps.getProduct().getCategory().getIdCategory().equals(fc))
                    .toList();
        }

        // filtro por pesquisa
        if (q != null && !q.isBlank()) {
            String term = q.trim().toLowerCase();
            products = products.stream()
                    .filter(ps -> ps.getProduct().getProductName().toLowerCase().contains(term)
                            || (ps.getProduct().getSku() != null && ps.getProduct().getSku().toLowerCase().contains(term)))
                    .toList();
        }

        Cart cart = getCart(session);

        String selectedCategoryName = null;
        if (categoria != null) {
            selectedCategoryName = categories.stream()
                    .filter(c -> c.getIdCategory().equals(categoria))
                    .map(c -> c.getCategoryName())
                    .findFirst().orElse(null);
        }

        model.addAttribute("store", store);
        model.addAttribute("allStores", storeRepository.findByActiveTrue());
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", categoria);
        model.addAttribute("selectedCategoryName", selectedCategoryName);
        model.addAttribute("q", q);
        model.addAttribute("cart", cart);
        model.addAttribute("addedProductId", added);
        return "catalogue";
    }

    @PostMapping("/carrinho/adicionar")
    public String addToCart(@PathVariable Long storeId,
                             @RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int qty,
                             @RequestParam(required = false) Long categoria,
                             HttpSession session) {

        productStoreRepository.findById(
                new pt.ipvc.kiosks.dal.entities.ProductStoreId(productId, storeId))
                .ifPresent(ps -> {
                    Cart cart = getCart(session);
                    cart.add(new CartItem(
                            ps.getProduct().getIdProduct(),
                            ps.getProduct().getProductName(),
                            ps.getProduct().getSku(),
                            ps.getProduct().getPrice(),
                            qty));
                    session.setAttribute("cart", cart);
                });

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
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) return "redirect:/";
        model.addAttribute("store", store);
        model.addAttribute("allStores", storeRepository.findByActiveTrue());
        model.addAttribute("cart", getCart(session));
        return "cart";
    }

    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) { cart = new Cart(); session.setAttribute("cart", cart); }
        return cart;
    }
}
