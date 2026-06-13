package pt.ipvc.kiosks.web.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pt.ipvc.kiosks.web.dto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class CoreApiClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${core.api.url:http://localhost:8090}")
    private String baseUrl;

    // ── Stores ──────────────────────────────────────────────────────────────

    public List<StoreDto> getActiveStores() {
        return getList("/api/stores?active=true", StoreDto.class);
    }

    public StoreDto getStore(Long id) {
        return rest.getForObject(baseUrl + "/api/stores/" + id, StoreDto.class);
    }

    // ── Categories ──────────────────────────────────────────────────────────

    public List<CategoryDto> getCategoriesByStore(Long storeId) {
        return getList("/api/categories?storeId=" + storeId + "&active=true", CategoryDto.class);
    }

    // ── Products ─────────────────────────────────────────────────────────────

    public List<ProductDto> getProducts(Long storeId, Long categoryId) {
        String url = "/api/products?storeId=" + storeId;
        if (categoryId != null) url += "&categoryId=" + categoryId;
        return getList(url, ProductDto.class);
    }

    // ── Orders ───────────────────────────────────────────────────────────────

    public OrderDto createOrder(Long kioskId, Map<String, Integer> items) {
        Map<String, Object> body = Map.of("kioskId", kioskId, "items", items);
        return rest.postForObject(baseUrl + "/api/orders", body, OrderDto.class);
    }

    // ── Kiosks ───────────────────────────────────────────────────────────────

    public List<pt.ipvc.kiosks.web.dto.KioskDto> getKiosksByStore(Long storeId) {
        return getList("/api/kiosks?storeId=" + storeId, pt.ipvc.kiosks.web.dto.KioskDto.class);
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private <T> List<T> getList(String path, Class<T> type) {
        ResponseEntity<List<T>> resp = rest.exchange(
                baseUrl + path, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<T>>() {});
        return resp.getBody() != null ? resp.getBody() : List.of();
    }
}
