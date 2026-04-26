package server.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PayPalCaptureResponseDTO {
    private Long narudzbinaId;
    private String status;
    private String providerOrderId;
}
