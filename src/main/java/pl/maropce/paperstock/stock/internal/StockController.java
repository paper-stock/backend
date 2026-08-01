package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.stock.internal.dto.StockDTO;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/search")
    public List<StockDTO> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return stockService.search(q, logoSize);
    }
}
