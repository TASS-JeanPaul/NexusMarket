package application.dialog.requests;

import application.data.entities.ProductReturnDetail;

import java.util.ArrayList;
import java.util.List;

public record ReturnRequest(Long buyerId, Long orderId, String reason, List<ReturnLineRequest> lines) {

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
