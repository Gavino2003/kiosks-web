package pt.ipvc.kiosks.web.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {

    private Long   productId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;
    private int    quantity;

    public CartItem(Long productId, String productName, String sku, BigDecimal unitPrice, int quantity) {
        this.productId   = productId;
        this.productName = productName;
        this.sku         = sku;
        this.unitPrice   = unitPrice;
        this.quantity    = quantity;
    }

    public Long       getProductId()   { return productId; }
    public String     getProductName() { return productName; }
    public String     getSku()         { return sku; }
    public BigDecimal getUnitPrice()   { return unitPrice; }
    public int        getQuantity()    { return quantity; }
    public void       setQuantity(int q) { this.quantity = q; }
    public BigDecimal getLineTotal()   { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
