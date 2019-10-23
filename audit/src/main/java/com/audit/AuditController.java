package com.audit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "audit-payment", produces = "application/json")
public class AuditController {

    @GetMapping("type1")
    public ResponseEntity logOnePayment() {
        //TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping("type2")
    public ResponseEntity logSecondPayment() {
        //TODO
        return ResponseEntity.ok().build();
    }
}
