package pt.ipvc.kiosks.web.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cart implements Serializable {

    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() { return items; }

    public void add(CartItem incoming) {
        Optional<CartItem> existing = items.stream()
                .filter(i -> i.getProductId().equals(incoming.getProductId()))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + incoming.getQuantity());
        } else {
            items.add(incoming);
        }
    }

    public void remove(Long productId) {
        items.removeIf(i -> i.getProductId().equals(productId));
    }

    public void clear() { items.clear(); }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() { return items.isEmpty(); }
}
