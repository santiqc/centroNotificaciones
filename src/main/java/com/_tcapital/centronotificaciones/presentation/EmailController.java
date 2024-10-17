package com._tcapital.centronotificaciones.presentation;

import com._tcapital.centronotificaciones.Infrastructure.exception.EmailSendException;
import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.application.Dto.EmailResponseDto;
import com._tcapital.centronotificaciones.application.Dto.RequestEmailDto;
import com._tcapital.centronotificaciones.application.EmailService;
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
    public ResponseEntity<EmailResponseDto> sendEmail(@RequestBody RequestEmailDto emailRequest) throws EmailSendException{
        return ResponseEntity.ok(emailService.sendEmail(emailRequest));
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
