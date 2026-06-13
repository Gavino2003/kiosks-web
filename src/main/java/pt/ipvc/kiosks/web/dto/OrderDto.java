package pt.ipvc.kiosks.web.dto;

import java.math.BigDecimal;

public class OrderDto {
    public Long       id;
    public String     reference;
    public String     status;
    public BigDecimal orderTotal;
    public Long       kioskId;
    public String     kioskName;
    public Long       storeId;
    public String     storeName;
}
