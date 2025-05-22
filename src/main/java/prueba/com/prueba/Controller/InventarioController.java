package prueba.com.prueba.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class InventarioController {

    @GetMapping("/status")
    public String getStatus() {
        return "Employee API is running! 🙌";
    }

}
