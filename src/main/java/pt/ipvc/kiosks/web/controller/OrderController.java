package pt.ipvc.kiosks.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.dal.entities.*;
import pt.ipvc.kiosks.dal.repository.*;
import pt.ipvc.kiosks.web.model.Cart;
import pt.ipvc.kiosks.web.model.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Controller
public class OrderController {

    @Autowired private OrderRepository   orderRepository;
    @Autowired private OrderLineRepository orderLineRepository;
    @Autowired private KioskRepository   kioskRepository;
    @Autowired private StoreRepository   storeRepository;
    @Autowired private ProductRepository productRepository;

    @PostMapping("/loja/{storeId}/checkout")
    public String checkout(@PathVariable Long storeId, HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/loja/" + storeId + "/carrinho";

        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) return "redirect:/";

        // escolhe um quiosque activo da loja (ou o primeiro disponível)
        List<Kiosk> kiosks = kioskRepository.findByStoreIdStore(storeId);
        Kiosk kiosk = kiosks.stream()
                .filter(k -> k.getStatus() == KioskStatus.ACTIVE)
                .findFirst()
                .orElse(kiosks.isEmpty() ? null : kiosks.get(0));

        if (kiosk == null) {
            model.addAttribute("store", store);
            model.addAttribute("cart", cart);
            model.addAttribute("error", "Nenhum quiosque disponível nesta loja.");
            return "cart";
        }

        // gera referência única
        String ref = generateReference();

        // cria encomenda
        Order order = new Order(ref, cart.getTotal(), kiosk);
        order = orderRepository.save(order);

        // cria linhas
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                OrderLine line = new OrderLine(item.getQuantity(), item.getUnitPrice(), product, order);
                orderLineRepository.save(line);
            }
        }

        String reference = order.getReference();
        BigDecimal total = order.getOrderTotal();
        cart.clear();
        session.removeAttribute("cart");
        session.removeAttribute("currentStoreId");

        model.addAttribute("reference", reference);
        model.addAttribute("total", total);
        model.addAttribute("store", store);
        model.addAttribute("itemCount", cart.getTotalItems());
        return "confirmation";
    }

    private String generateReference() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder("ORD-").append(year).append("-");
        for (int i = 0; i < 8; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        String ref = sb.toString();
        // garantir unicidade
        while (orderRepository.findByReference(ref).isPresent()) {
            sb = new StringBuilder("ORD-").append(year).append("-");
            for (int i = 0; i < 8; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
            ref = sb.toString();
        }
        return ref;
    }
}
