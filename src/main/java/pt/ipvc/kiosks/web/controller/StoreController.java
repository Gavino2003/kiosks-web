package pt.ipvc.kiosks.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.repository.StoreRepository;

import java.util.List;

@Controller
public class StoreController {

    @Autowired private StoreRepository storeRepository;

    @GetMapping("/")
    public String index() {
        List<Store> stores = storeRepository.findByActiveTrue();
        if (stores.isEmpty()) return "index";
        return "redirect:/loja/" + stores.get(0).getIdStore();
    }
}
