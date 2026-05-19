package com.re.cinema_manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {
    
    private String title;
    
    private String description;
    
    // Đảm bảo khớp với logic "thời lượng > 0" ở database
    private int durationMinutes; 
    
    private LocalDate releaseDate;
    
    // Link ảnh bìa (poster) mà bạn vừa bổ sung rất hay lúc nãy
    private String posterUrl;
    
    // Chú ý: Ta chỉ cần nhận ID của thể loại từ form của Admin, không nhận cả Object
    private Integer genreId; 
}
