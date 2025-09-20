package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull
    private Integer id;
    @NotBlank
    @Size(max = 200)
    private String content;
    private Boolean isPositive;

    public boolean hasContent() {
        return !(content == null || content.isBlank());
    }

    public boolean hasIsPositive() {
        return !(isPositive == null);
    }
}
