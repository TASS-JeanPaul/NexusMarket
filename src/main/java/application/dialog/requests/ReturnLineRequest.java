package application.dialog.requests;

import application.data.entities.ProductReturnDetail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ReturnLineRequest(@NotNull Long orderDetailId, @Positive int returnedQuantity, String notes) {

    public ProductReturnDetail toDetail() {
        ProductReturnDetail detail = new ProductReturnDetail();
        detail.setOrderDetailId(orderDetailId);
        detail.setReturnedQuantity(returnedQuantity);
        detail.setNotes(notes);
        return detail;
    }
}
