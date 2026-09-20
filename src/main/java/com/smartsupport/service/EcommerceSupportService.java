package com.smartsupport.service;

import com.smartsupport.entity.*;
import com.smartsupport.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.*;

@Service
public class EcommerceSupportService {
    private final OrderRepository orders; private final PaymentRepository payments; private final RefundRepository refunds;
    private final ReturnRequestRepository returns; private final ProductRepository products; private final CouponRepository coupons;
    public EcommerceSupportService(OrderRepository o, PaymentRepository p, RefundRepository r, ReturnRequestRepository rr, ProductRepository pr, CouponRepository c){orders=o;payments=p;refunds=r;returns=rr;products=pr;coupons=c;}

    public String buildContext(String message){
        StringBuilder c=new StringBuilder("Verified e-commerce data from SmartSupport database:\n");
        String orderNo=extractOrder(message);
        if(orderNo!=null){
            Optional<Order> oo=orders.findByOrderNumberIgnoreCase(orderNo);
            if(oo.isPresent()){
                Order o=oo.get(); c.append("ORDER ").append(o.getOrderNumber()).append(": product=").append(o.getProductName()).append(", amount=₹").append(o.getAmount()).append(", status=").append(o.getStatus()).append(", deliveryStatus=").append(o.getDeliveryStatus()).append(", location=").append(o.getCurrentLocation()).append(", expectedDelivery=").append(o.getExpectedDelivery()).append(", deliveredDate=").append(o.getDeliveredDate()).append(", returnEligible=").append(o.getReturnEligible()).append(", exchangeEligible=").append(o.getExchangeEligible()).append('\n');
                payments.findByOrder_OrderNumberIgnoreCase(orderNo).ifPresent(p->c.append("PAYMENT: status=").append(p.getStatus()).append(", amount=₹").append(p.getAmount()).append(", amountDebited=").append(p.getAmountDebited()).append(", method=").append(p.getMethod()).append(", transactionId=").append(p.getTransactionId()).append('\n'));
                refunds.findByOrder_OrderNumberIgnoreCase(orderNo).ifPresent(r->c.append("REFUND: status=").append(r.getStatus()).append(", amount=₹").append(r.getAmount()).append(", initiatedDate=").append(r.getInitiatedDate()).append(", completedDate=").append(r.getCompletedDate()).append('\n'));
                returns.findByOrder_OrderNumberIgnoreCase(orderNo).ifPresent(r->c.append("RETURN/EXCHANGE: type=").append(r.getType()).append(", status=").append(r.getStatus()).append(", reason=").append(r.getReason()).append('\n'));
            } else c.append("No order found for ").append(orderNo).append(". Do not invent order details.\n");
        }
        return c.toString();
    }
    public String deterministic(String message){
        String in=message==null?"":message.toLowerCase(Locale.ROOT);
        String orderNo=extractOrder(message);
        if(orderNo!=null){ Optional<Order> oo=orders.findByOrderNumberIgnoreCase(orderNo); if(oo.isPresent()){
            Order o=oo.get();
            if(contains(in,"payment","paid","debit","deducted","transaction")) return paymentAnswer(o, orderNo);
            if(contains(in,"refund","money back","paise wapas")) return refundAnswer(orderNo);
            if(contains(in,"return","wapis","wapas")) return returnAnswer(o,orderNo,false);
            if(contains(in,"exchange","size change","replace")) return returnAnswer(o,orderNo,true);
            if(contains(in,"track","where is","delivery","delivered","late","delay","order")) return orderAnswer(o);
            if(contains(in,"cancel")) return cancelAnswer(o);
        }}
        if(contains(in,"payment failed","payment fail","paise kat","money deducted","amount deducted","debited")) return paymentWithoutOrder();
        if(contains(in,"refund")) return "Sure, I can check your refund. Please share your Order ID (for example, ORD1001).";
        if(contains(in,"return","exchange")) return "I can check return or exchange eligibility. Please share your Order ID.";
        if(contains(in,"track order","where is my order","order status","delivery")) return "I can check the live order record. Please share your Order ID (for example, ORD1001).";
        if(contains(in,"coupon","promo","discount","offer")){ String code=extractCoupon(message); if(code!=null) return couponAnswer(code); return "I can check a coupon for you. Please share the coupon code."; }
        if(contains(in,"product","size","available","availability","shopping","buy")) return productHelp(message);
        return null;
    }
    private String paymentAnswer(Order o,String no){ Optional<Payment> op=payments.findByOrder_OrderNumberIgnoreCase(no); if(op.isEmpty()) return "I couldn't find a payment record for "+no+"."; Payment p=op.get();
        if("FAILED".equalsIgnoreCase(p.getStatus()) && Boolean.TRUE.equals(p.getAmountDebited())){ Optional<Refund> r=refunds.findByOrder_OrderNumberIgnoreCase(no); if(r.isPresent()) return "I checked order "+no+". The payment failed, ₹"+p.getAmount()+" was debited, and the refund status is "+r.get().getStatus()+". I won't guess a completion date; the latest status is what is recorded in your account."; return "I checked order "+no+". The payment failed and ₹"+p.getAmount()+" was debited. No refund record is currently available, so this should be escalated to support."; }
        return "I checked order "+no+". Payment status: "+p.getStatus()+" for ₹"+p.getAmount()+"."; }
    private String paymentWithoutOrder(){return "I can check whether the amount was actually debited and whether a refund was created. Please share your Order ID so I can verify the transaction instead of guessing.";}
    private String refundAnswer(String no){ Optional<Refund> r=refunds.findByOrder_OrderNumberIgnoreCase(no); if(r.isEmpty()) return "I checked "+no+" but there is no refund record currently available."; Refund x=r.get(); return "For order "+no+", the refund status is "+x.getStatus()+" for ₹"+x.getAmount()+"."; }
    private String returnAnswer(Order o,String no,boolean exchange){ boolean eligible=exchange?Boolean.TRUE.equals(o.getExchangeEligible()):Boolean.TRUE.equals(o.getReturnEligible()); Optional<ReturnRequest> rr=returns.findByOrder_OrderNumberIgnoreCase(no); if(rr.isPresent()) return "For order "+no+", your "+rr.get().getType().toLowerCase()+" request is currently "+rr.get().getStatus()+"."; return eligible?"Order "+no+" is currently eligible for "+(exchange?"exchange":"return")+". I can guide you through the next step.":"Order "+no+" is not currently marked as eligible for "+(exchange?"exchange":"return")+"."; }
    private String orderAnswer(Order o){return "Order "+o.getOrderNumber()+" is currently "+o.getDeliveryStatus()+". Current location: "+o.getCurrentLocation()+". Expected delivery: "+o.getExpectedDelivery()+".";}
    private String cancelAnswer(Order o){return Boolean.TRUE.equals(o.getCancelled())?"Order "+o.getOrderNumber()+" is already cancelled.":("Order "+o.getOrderNumber()+" is currently "+o.getStatus()+". I can only confirm the current status here; cancellation eligibility should be checked before placing a cancellation request.");}
    private String couponAnswer(String code){ Optional<Coupon> c=coupons.findByCodeIgnoreCase(code); if(c.isEmpty()) return "Coupon "+code+" was not found in the current database."; Coupon x=c.get(); return x.getActive()?"Coupon "+x.getCode()+" is active: "+x.getDiscountPercent()+"% off, with a minimum order of ₹"+x.getMinimumOrder()+".":"Coupon "+x.getCode()+" is currently inactive."; }
    private String productHelp(String m){ List<Product> ps=products.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(m,m); if(ps.isEmpty()) return "I can help with product availability, sizes and shopping questions. Tell me the product name or brand you are looking for."; Product p=ps.get(0); return p.getName()+" by "+p.getBrand()+" is "+(p.getInStock()?"currently in stock":"currently out of stock")+" at ₹"+p.getPrice()+". Available sizes: "+p.getSizes()+"."; }
    public String extractOrder(String s){ if(s==null)return null; Matcher m=Pattern.compile("\\bORD[- ]?[A-Z0-9]{3,12}\\b",Pattern.CASE_INSENSITIVE).matcher(s); return m.find()?m.group().replace(" ","").toUpperCase(Locale.ROOT):null; }
    private String extractCoupon(String s){ if(s==null)return null; Matcher m=Pattern.compile("\\b[A-Z][A-Z0-9_-]{3,14}\\b").matcher(s.toUpperCase(Locale.ROOT)); while(m.find()){String x=m.group(); if(x.startsWith("ORD"))continue; return x;} return null; }
    private boolean contains(String s,String... a){for(String x:a)if(s.contains(x))return true;return false;}
}
