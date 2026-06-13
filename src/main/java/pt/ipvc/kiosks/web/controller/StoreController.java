package pt.ipvc.kiosks.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pt.ipvc.kiosks.web.client.CoreApiClient;
import pt.ipvc.kiosks.web.dto.StoreDto;

import java.util.List;

@Controller
public class StoreController {

    @Autowired private CoreApiClient api;

    @GetMapping("/")
    public String index() {
        List<StoreDto> stores = api.getActiveStores();
        if (stores.isEmpty()) return "index";
        return "redirect:/loja/" + stores.get(0).id;
    }
}
