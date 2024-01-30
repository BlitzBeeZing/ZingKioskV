package app.zingnow.zingkiosk.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class UpiResponse {
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getTxnUuid() {
        return txnUuid;
    }

    public void setTxnUuid(String txnUuid) {
        this.txnUuid = txnUuid;
    }

    public OfferDetails getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(OfferDetails offerDetails) {
        this.offerDetails = offerDetails;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @SerializedName("order_id")
    private String orderId;

    private String status;

    private Payment payment;

    @SerializedName("txn_uuid")
    private String txnUuid;

    @SerializedName("offer_details")
    private OfferDetails offerDetails;

    @SerializedName("txn_id")
    private String txnId;

    // Add getters and setters as needed

    public static class Payment {
        public SdkParams getSdkParams() {
            return sdkParams;
        }

        public void setSdkParams(SdkParams sdkParams) {
            this.sdkParams = sdkParams;
        }

        public Authentication getAuthentication() {
            return authentication;
        }

        public void setAuthentication(Authentication authentication) {
            this.authentication = authentication;
        }

        @SerializedName("sdk_params")
        private SdkParams sdkParams;

        private Authentication authentication;

        // Add getters and setters as needed
    }

    public static class SdkParams {
        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCustomerLastName() {
            return customerLastName;
        }

        public void setCustomerLastName(String customerLastName) {
            this.customerLastName = customerLastName;
        }

        public String getCustomerFirstName() {
            return customerFirstName;
        }

        public void setCustomerFirstName(String customerFirstName) {
            this.customerFirstName = customerFirstName;
        }

        public String getMerchantVpa() {
            return merchantVpa;
        }

        public void setMerchantVpa(String merchantVpa) {
            this.merchantVpa = merchantVpa;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        public String getMcc() {
            return mcc;
        }

        public void setMcc(String mcc) {
            this.mcc = mcc;
        }

        public String getTr() {
            return tr;
        }

        public void setTr(String tr) {
            this.tr = tr;
        }

        private String amount;

        @SerializedName("customer_last_name")
        private String customerLastName;

        @SerializedName("customer_first_name")
        private String customerFirstName;

        @SerializedName("merchant_vpa")
        private String merchantVpa;

        @SerializedName("merchant_name")
        private String merchantName;

        private String mcc;
        private String tr;

        // Add getters and setters as needed
    }

    public static class Authentication {
        private String url;
        private String method;

        // Add getters and setters as needed
    }

    public static class OfferDetails {
        public String[] getOffers() {
            return offers;
        }

        public void setOffers(String[] offers) {
            this.offers = offers;
        }

        private String[] offers;

        // Add getters and setters as needed
    }
}
