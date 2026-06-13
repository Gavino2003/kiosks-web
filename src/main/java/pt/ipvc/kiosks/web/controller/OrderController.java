package pt.ipvc.kiosks.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.web.client.CoreApiClient;
import pt.ipvc.kiosks.web.dto.KioskDto;
import pt.ipvc.kiosks.web.dto.OrderDto;
import pt.ipvc.kiosks.web.dto.StoreDto;
import pt.ipvc.kiosks.web.model.Cart;
import pt.ipvc.kiosks.web.model.CartItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired private CoreApiClient api;

    @PostMapping("/loja/{storeId}/checkout")
    public String checkout(@PathVariable Long storeId, HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/loja/" + storeId + "/carrinho";

        StoreDto store = api.getStore(storeId);
        if (store == null) return "redirect:/";

        // escolhe primeiro quiosque ACTIVE da loja
        List<KioskDto> kiosks = api.getKiosksByStore(storeId);
        KioskDto kiosk = kiosks.stream()
                .filter(k -> "ACTIVE".equals(k.status))
                .findFirst()
                .orElse(kiosks.isEmpty() ? null : kiosks.get(0));

        if (kiosk == null) {
            model.addAttribute("store", store);
            model.addAttribute("allStores", api.getActiveStores());
            model.addAttribute("cart", cart);
            model.addAttribute("error", "Nenhum quiosque disponível nesta loja.");
            return "cart";
        }

        // constrói mapa productId → quantidade
        Map<String, Integer> items = new HashMap<>();
        for (CartItem item : cart.getItems()) {
            items.put(String.valueOf(item.getProductId()), item.getQuantity());
        }

        try {
            OrderDto order = api.createOrder(kiosk.id, items);

            cart.clear();
            session.removeAttribute("cart");
            session.removeAttribute("currentStoreId");

            model.addAttribute("reference", order.reference);
            model.addAttribute("total",     order.orderTotal);
            model.addAttribute("store",     store);
            return "confirmation";
        } catch (Exception e) {
            model.addAttribute("store",     store);
            model.addAttribute("allStores", api.getActiveStores());
            model.addAttribute("cart",      cart);
            model.addAttribute("error",     "Erro ao criar encomenda: " + e.getMessage());
            return "cart";
        }
    }
}
