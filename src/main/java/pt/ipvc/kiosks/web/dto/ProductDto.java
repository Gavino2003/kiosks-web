package pt.ipvc.kiosks.web.dto;

import java.math.BigDecimal;

public class ProductDto {
    public Long       id;
    public String     productName;
    public String     description;
    public BigDecimal price;
    public String     sku;
    public String     imageUrl;
    public Boolean    active;
    public Long       categoryId;
    public String     categoryName;
    public Integer    stockQuantity;
}
