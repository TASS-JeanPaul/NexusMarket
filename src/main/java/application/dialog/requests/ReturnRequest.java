package application.dialog.requests;

import application.data.entities.ProductReturnDetail;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ReturnRequest(@NotNull Long buyerId, @NotNull Long orderId, @NotBlank String reason, List<ReturnLineRequest> lines) {

    public List<ProductReturnDetail> toDetails() {
        List<ProductReturnDetail> details = new ArrayList<>();

        if (lines != null) {
            for (ReturnLineRequest line : lines) {
                details.add(line.toDetail());
            }
        }
        return details;
    }
}
