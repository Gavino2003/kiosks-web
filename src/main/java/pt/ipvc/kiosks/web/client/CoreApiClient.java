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
        ResponseEntity<List<StoreDto>> r = rest.exchange(
                baseUrl + "/api/stores?active=true", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<StoreDto>>() {});
        return r.getBody() != null ? r.getBody() : List.of();
    }

    public StoreDto getStore(Long id) {
        try { return rest.getForObject(baseUrl + "/api/stores/" + id, StoreDto.class); }
        catch (Exception e) { return null; }
    }

    // ── Categories ──────────────────────────────────────────────────────────

    public List<CategoryDto> getCategoriesByStore(Long storeId) {
        ResponseEntity<List<CategoryDto>> r = rest.exchange(
                baseUrl + "/api/categories?storeId=" + storeId + "&active=true",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CategoryDto>>() {});
        return r.getBody() != null ? r.getBody() : List.of();
    }

    // ── Products ─────────────────────────────────────────────────────────────

    public List<ProductDto> getProducts(Long storeId, Long categoryId) {
        String url = baseUrl + "/api/products?storeId=" + storeId;
        if (categoryId != null) url += "&categoryId=" + categoryId;
        ResponseEntity<List<ProductDto>> r = rest.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ProductDto>>() {});
        return r.getBody() != null ? r.getBody() : List.of();
    }

    // ── Orders ───────────────────────────────────────────────────────────────

    public OrderDto createOrder(Long kioskId, Map<String, Integer> items) {
        Map<String, Object> body = Map.of("kioskId", kioskId, "items", items);
        return rest.postForObject(baseUrl + "/api/orders", body, OrderDto.class);
    }

    // ── Kiosks ───────────────────────────────────────────────────────────────

    public List<KioskDto> getKiosksByStore(Long storeId) {
        ResponseEntity<List<KioskDto>> r = rest.exchange(
                baseUrl + "/api/kiosks?storeId=" + storeId,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<KioskDto>>() {});
        return r.getBody() != null ? r.getBody() : List.of();
    }
}
