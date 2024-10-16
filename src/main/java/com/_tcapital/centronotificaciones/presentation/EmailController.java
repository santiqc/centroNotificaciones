package com._tcapital.centronotificaciones.presentation;

import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.application.EmailService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailDto emailRequest) {
        emailService.sendEmail(emailRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmailDto>> getSentEmails(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nroDocument,
            @RequestParam(required = false) String process,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<EmailDto> emails = emailService.getSentEmails(status, nroDocument, process, pageNo, pageSize);
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/")
    public ResponseEntity<String> getindex() {
        String s = "email";
        return ResponseEntity.ok("email run");
    }


}
