package com.smartsupport.config;

import com.smartsupport.entity.*;
import com.smartsupport.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner init(BusinessRepository businesses, FaqRepository faqs, CustomerRepository customers,
                           OrderRepository orders, PaymentRepository payments, RefundRepository refunds,
                           ReturnRequestRepository returns, ProductRepository products, CouponRepository coupons) {
        return args -> {
            Business b;
            if (businesses.count() == 0) {
                b = new Business(); b.setName("SmartSupport Store"); b.setPhone("+91 98765 43210"); b.setEmail("support@smartsupport.local");
                b.setHours("Monday-Sunday, 9:00 AM - 9:00 PM"); b.setDescription("AI-powered e-commerce customer support demo store."); businesses.save(b);
            }
            if (faqs.count() == 0) {
                add(faqs,"How do I track my order?","track,tracking,order status,where is my order,delivery","Share your Order ID and I can check the current order record.");
                add(faqs,"How do I return a product?","return,returns,return product,send back","Share your Order ID and I can check return eligibility.");
                add(faqs,"How does exchange work?","exchange,size,replace,wrong size","Share your Order ID and I can check exchange eligibility.");
                add(faqs,"Payment failed but money was deducted","payment failed,amount deducted,money deducted,debited","Share your Order ID so I can verify the payment and refund records.");
                add(faqs,"How can I check my refund?","refund,refund status,money back","Share your Order ID so I can verify the latest refund status.");
                add(faqs,"How can I contact support?","contact,phone,email,support,customer care","You can contact support at {phone} or {email}.");
                add(faqs,"What are your support hours?","hours,timing,open,opening,working time","Our support hours are Monday to Sunday, 9 AM to 9 PM.");
                add(faqs,"Coupon not working","coupon,promo,discount,offer","Share the coupon code and I can check whether it exists and is active.");
            }
            if(customers.count()==0){
                Customer c=new Customer(); c.setName("Demo Customer"); c.setEmail("demo@smartsupport.local"); c.setPhone("9999999999"); customers.save(c);
                Order o1=order(c,"ORD1001","Premium Running Shoes","9","2499.00","SHIPPED","IN_TRANSIT","Durgapur Hub",LocalDate.now().minusDays(2),LocalDate.now().plusDays(1),null,true,true,false); orders.save(o1);
                Payment p1=payment(o1,"2499.00","UPI","SUCCESS",true,"TXN1001"); payments.save(p1);
                Order o2=order(c,"ORD1002","Classic Denim Jacket","M","1899.00","PAYMENT_FAILED","NOT_CREATED","Payment Gateway",LocalDate.now(),null,null,false,false,false); orders.save(o2);
                Payment p2=payment(o2,"1899.00","CARD","FAILED",true,"TXN1002"); payments.save(p2);
                Refund r2=refund(o2,"1899.00","INITIATED","Payment failed after debit",LocalDate.now(),null); refunds.save(r2);
                Order o3=order(c,"ORD1003","Cotton Shirt","L","1299.00","DELIVERED","DELIVERED","Customer Address",LocalDate.now().minusDays(8),LocalDate.now().minusDays(4),LocalDate.now().minusDays(4),true,true,false); orders.save(o3);
                ReturnRequest rr=ret(o3,"RETURN","REQUESTED","Size issue",LocalDate.now().minusDays(1)); returns.save(rr);
                Order o4=order(c,"ORD1004","Casual Sneakers","8","2199.00","DELIVERED","DELIVERED","Customer Address",LocalDate.now().minusDays(15),LocalDate.now().minusDays(10),LocalDate.now().minusDays(10),false,false,false); orders.save(o4);
                Refund r4=refund(o4,"2199.00","COMPLETED","Approved refund",LocalDate.now().minusDays(7),LocalDate.now().minusDays(3)); refunds.save(r4);
            }
            if(products.count()==0){ product(products,"AirFlex Running Shoes","Footwear","SmartSport","2499.00",true,"7,8,9,10","Lightweight running shoes for everyday use."); product(products,"Classic Denim Jacket","Jackets","UrbanWear","1899.00",true,"S,M,L,XL","Classic fit denim jacket."); product(products,"Cotton Shirt","Shirts","DailyStyle","1299.00",true,"S,M,L,XL","Regular fit cotton shirt."); }
            if(coupons.count()==0){ coupon(coupons,"WELCOME10","10","999",true,"10% off on orders above ₹999"); coupon(coupons,"FESTIVE20","20","2999",true,"20% off on orders above ₹2999"); }
        };
    }
    private void add(FaqRepository r,String q,String k,String a){Faq f=new Faq();f.setQuestion(q);f.setKeywords(k);f.setAnswer(a);r.save(f);}
    private Order order(Customer c,String n,String prod,String size,String amount,String status,String ds,String loc,LocalDate od,LocalDate ed,LocalDate dd,boolean ret,boolean ex,boolean cancel){Order o=new Order();o.setCustomer(c);o.setOrderNumber(n);o.setProductName(prod);o.setSize(size);o.setAmount(new BigDecimal(amount));o.setStatus(status);o.setDeliveryStatus(ds);o.setCurrentLocation(loc);o.setOrderDate(od);o.setExpectedDelivery(ed);o.setDeliveredDate(dd);o.setReturnEligible(ret);o.setExchangeEligible(ex);o.setCancelled(cancel);return o;}
    private Payment payment(Order o,String amount,String method,String status,boolean debited,String txn){Payment p=new Payment();p.setOrder(o);p.setAmount(new BigDecimal(amount));p.setMethod(method);p.setStatus(status);p.setAmountDebited(debited);p.setTransactionId(txn);return p;}
    private Refund refund(Order o,String amount,String status,String reason,LocalDate init,LocalDate done){Refund r=new Refund();r.setOrder(o);r.setAmount(new BigDecimal(amount));r.setStatus(status);r.setReason(reason);r.setInitiatedDate(init);r.setCompletedDate(done);return r;}
    private ReturnRequest ret(Order o,String type,String status,String reason,LocalDate date){ReturnRequest r=new ReturnRequest();r.setOrder(o);r.setType(type);r.setStatus(status);r.setReason(reason);r.setRequestedDate(date);return r;}
    private void product(ProductRepository r,String n,String cat,String brand,String price,boolean stock,String sizes,String desc){Product p=new Product();p.setName(n);p.setCategory(cat);p.setBrand(brand);p.setPrice(new BigDecimal(price));p.setInStock(stock);p.setSizes(sizes);p.setDescription(desc);r.save(p);}
    private void coupon(CouponRepository r,String code,String pct,String min,boolean active,String desc){Coupon c=new Coupon();c.setCode(code);c.setDiscountPercent(new BigDecimal(pct));c.setMinimumOrder(new BigDecimal(min));c.setActive(active);c.setDescription(desc);r.save(c);}
}
