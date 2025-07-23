package server.DTOs;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ZanrDTO {
    private Long id;

    @Column(nullable = false, unique = true)
    private String naziv;
    

    private List<PlocaDTO> ploce;
    

	private Boolean vidljiv;
}
