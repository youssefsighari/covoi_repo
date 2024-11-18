package com.CarGo.dto;

public class UpdateRequestStatusRequest {
    private Long requestId;
    private String status;

    
    public UpdateRequestStatusRequest() {}

    public UpdateRequestStatusRequest(Long requestId, String status) {
        this.requestId = requestId;
        this.status = status;
    }

    
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
