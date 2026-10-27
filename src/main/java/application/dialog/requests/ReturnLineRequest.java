package application.dialog.requests;

import application.data.entities.ProductReturnDetail;

public record ReturnLineRequest(Long orderDetailId, int returnedQuantity, String notes) {

    public ProductReturnDetail toDetail() {
        ProductReturnDetail detail = new ProductReturnDetail();
        detail.setOrderDetailId(orderDetailId);
        detail.setReturnedQuantity(returnedQuantity);
        detail.setNotes(notes);
        return detail;
    }
}
