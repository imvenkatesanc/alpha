package LongCoding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FarmerApp {

    static class Customer {
        int CustomerID;
        String CustomerName;
        String Email;
        double Phone;
        String Address;

        public Customer(int customerID, String customerName, String email, double phone, String address) {
            CustomerID = customerID;
            CustomerName = customerName;
            Email = email;
            Phone = phone;
            Address = address;
        }

        public int getCustomerID() {
            return CustomerID;
        }

        public void setCustomerID(int customerID) {
            CustomerID = customerID;
        }

        public String getCustomerName() {
            return CustomerName;
        }

        public void setCustomerName(String customerName) {
            CustomerName = customerName;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public double getPhone() {
            return Phone;
        }

        public void setPhone(double phone) {
            Phone = phone;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        @Override
        public String toString() {
            return "Customer [CustomerID=" + CustomerID + ", CustomerName=" + CustomerName + ", Email=" + Email
                    + ", Phone=" + Phone + ", Address=" + Address + "]";
        }

    }

    static class Farmer {
        int FarmerID;
        String FarmerName;
        double PhoneNumber;
        String Address;
        String BankDetails;

        public Farmer(int farmerID, String farmerName, double phoneNumber, String address, String bankDetails) {
            FarmerID = farmerID;
            FarmerName = farmerName;
            PhoneNumber = phoneNumber;
            Address = address;
            BankDetails = bankDetails;
        }

        public int getFarmerID() {
            return FarmerID;
        }

        public void setFarmerID(int farmerID) {
            FarmerID = farmerID;
        }

        public String getFarmerName() {
            return FarmerName;
        }

        public void setFarmerName(String farmerName) {
            FarmerName = farmerName;
        }

        public double getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(double phoneNumber) {
            PhoneNumber = phoneNumber;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getBankDetails() {
            return BankDetails;
        }

        public void setBankDetails(String bankDetails) {
            BankDetails = bankDetails;
        }

        @Override
        public String toString() {
            return "Farmer [FarmerID=" + FarmerID + ", FarmerName=" + FarmerName + ", PhoneNumber=" + PhoneNumber
                    + ", Address=" + Address + ", BankDetails=" + BankDetails + "]";
        }

    }

    static class Product {
        int ProductID;
        String ProductName;
        String Category;
        double Price;
        String Description;
        boolean isAvailable;
        Farmer farmer;

        public Product(int productID, String productName, String category, double price, String description,
                boolean isAvailable, Farmer farmer) {
            ProductID = productID;
            ProductName = productName;
            Category = category;
            Price = price;
            Description = description;
            isAvailable = isAvailable;
            this.farmer = farmer;
        }

        public int getProductID() {
            return ProductID;
        }

        public void setProductID(int productID) {
            ProductID = productID;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String productName) {
            ProductName = productName;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String category) {
            Category = category;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double price) {
            Price = price;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public boolean getAvailable() {
            return isAvailable;
        }

        public void setAvailable(boolean isAvailable) {
            isAvailable = isAvailable;
        }

        public Farmer getFarmer() {
            return farmer;
        }

        public void setFarmer(Farmer farmer) {
            this.farmer = farmer;
        }

        @Override
        public String toString() {
            return "Product [ProductID=" + ProductID + ", ProductName=" + ProductName + ", Category=" + Category
                    + ", Price=" + Price + ", Description=" + Description + ", Quantity=" + isAvailable + ", farmer="
                    + farmer + "]";
        }

    }

    static class OrderItem {
        int OrderItemID;
        Product product;
        int Quantity;
        double price;

        public int getOrderItemID() {
            return OrderItemID;
        }

        public void setOrderItemID(int orderItemID) {
            OrderItemID = orderItemID;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int quantity) {
            Quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public OrderItem(int orderItemID, Product product, int quantity, double price) {
            OrderItemID = orderItemID;
            this.product = product;
            Quantity = quantity;
            this.price = price;
        }

        @Override
        public String toString() {
            return "OrderItem [OrderItemID=" + OrderItemID + ", product=" + product + ", Quantity=" + Quantity
                    + ", price=" + price + "]";
        }
    }

    static class Payment {
        int PaymentID;
        OrderItem orderItem;
        String PaymentStatus;
        LocalDate PaymentDate;

        public Payment(int paymentID, OrderItem orderItem, String paymentStatus, LocalDate paymentDate) {
            PaymentID = paymentID;
            this.orderItem = orderItem;
            PaymentStatus = paymentStatus;
            PaymentDate = paymentDate;
        }

        public int getPaymentID() {
            return PaymentID;
        }

        public void setPaymentID(int paymentID) {
            PaymentID = paymentID;
        }

        public OrderItem getOrder() {
            return orderItem;
        }

        public void setOrder(OrderItem orderItem) {
            this.orderItem = orderItem;
        }

        public String getPaymentStatus() {
            return PaymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            PaymentStatus = paymentStatus;
        }

        public LocalDate getPaymentDate() {
            return PaymentDate;
        }

        public void setPaymentDate(LocalDate paymentDate) {
            PaymentDate = paymentDate;
        }

        @Override
        public String toString() {
            return "Payment [PaymentID=" + PaymentID + ", order=" + orderItem + ", PaymentStatus=" + PaymentStatus
                    + ", PaymentDate=" + PaymentDate + "]";
        }

    }

    static class Order {
        int OrderID;
        Customer customer;
        OrderItem orderItem;
        LocalDate OrderDate;
        String Status;
        double TotalAmount;
        Payment payment;

        public int getOrderID() {
            return OrderID;
        }

        public void setOrderID(int orderID) {
            OrderID = orderID;
        }

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public OrderItem getOrderItem() {
            return orderItem;
        }

        public void setOrderItem(OrderItem orderItem) {
            this.orderItem = orderItem;
        }

        public LocalDate getOrderDate() {
            return OrderDate;
        }

        public void setOrderDate(LocalDate orderDate) {
            OrderDate = orderDate;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public double getTotalAmount() {
            return TotalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            TotalAmount = totalAmount;
        }

        public Payment getPayment() {
            return payment;
        }

        public void setPayment(Payment payment) {
            this.payment = payment;
        }

        public Order(int orderID, Customer customer, OrderItem orderItem, LocalDate orderDate, String status,
                double totalAmount, Payment payment) {
            OrderID = orderID;
            this.customer = customer;
            this.orderItem = orderItem;
            OrderDate = orderDate;
            Status = status;
            TotalAmount = totalAmount;
            this.payment = payment;
        }

        @Override
        public String toString() {
            return "Order [OrderID=" + OrderID + ", customer=" + customer + ", orderItem=" + orderItem + ", OrderDate="
                    + OrderDate + ", Status=" + Status + ", TotalAmount=" + TotalAmount + ", payment=" + payment + "]";
        }
    }

    static class Review {
        int ReviewID;
        String Feedback;
        Customer customer;
        Product product;

        public Review(int reviewID, String feedback, Customer customer, Product product) {
            ReviewID = reviewID;
            Feedback = feedback;
            this.customer = customer;
            this.product = product;
        }

        public int getReviewID() {
            return ReviewID;
        }

        public void setReviewID(int reviewID) {
            ReviewID = reviewID;
        }

        public String getFeedback() {
            return Feedback;
        }

        public void setFeedback(String feedback) {
            Feedback = feedback;
        }

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        @Override
        public String toString() {
            return "Review [ReviewID=" + ReviewID + ", Feedback=" + Feedback + ", customer=" + customer + ", product="
                    + product + "]";
        }

    }

    // Storing
    static Map<Integer, Customer> customerMap = new HashMap<>();
    static Map<Integer, Farmer> farmerMap = new HashMap<>();
    static Map<Integer, Order> orderMap = new HashMap<>();
    static List<Product> products = new ArrayList<>();
    static List<OrderItem> orderItemsList = new ArrayList<>();
    static List<Review> reviews = new ArrayList<>();
    static List<Payment> payments = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    // Services
    static void createCustomer(Customer customer) {
        customerMap.put(customer.getCustomerID(), customer);
    }

    static void createFarmer(Farmer farmer) {
        farmerMap.put(farmer.getFarmerID(), farmer);
    }

    static void makeOrder(OrderItem orderItem) {
        orderItemsList.add(orderItem);
    }

    static void makePayment(Payment payment) {
        payments.add(payment);
    }

    static void addReview(Review review) {
        reviews.add(review);
    }

    static void addProduct(Product product) {
        products.add(product);
    }

    static double getTotalAmount() {
        return orderItemsList.stream()
                .mapToDouble(o -> o.getPrice() * o.getQuantity())
                .sum();
    }

    static Farmer getFarmerByID() {
        Farmer farmer = farmerMap.get(getFarmerID());
        return farmer;
    }

    static int getFarmerID() {
        System.out.println("Enter Farmer ID: ");
        int farmerID = sc.nextInt();
        return farmerID;
    }

    static int getProductID() {
        System.out.println("Enter Product ID: ");
        int productID = sc.nextInt();
        return productID;
    }

    static void isOrder(int orderItemID, int paymentID){
        if ((orderItemsList.stream().filter(o -> o.getOrderItemID() == orderItemID).findFirst().orElse(null) != null) &&
        (payments.stream().filter(p -> p.getPaymentID() == paymentID).findFirst().orElse(null) != null)) {
        
        System.out.println("Enter OrderID: ");
        int OrderID = sc.nextInt();
        System.out.println("Enter Customer ID: ");
        sc.nextLine(); // Consume newline
        int customerID = sc.nextInt();
        sc.nextLine(); // Consume newline
        Customer customer = customerMap.get(customerID);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        OrderItem orderItem = orderItemsList.stream().filter(o -> o.getOrderItemID() == orderItemID).findFirst().orElse(null);

        if (orderItem == null) {
            System.out.println("Order item not found.");
            return;
        }

        LocalDate OrderDate = LocalDate.now();
        String Status = "Order Confirmed";
        double TotalAmount = getTotalAmount();
        Payment payment = new Payment(paymentID, orderItem, Status, OrderDate);

        orderMap.put(OrderID, new Order(OrderID, customer, orderItem, OrderDate, Status, TotalAmount, payment));
        System.out.println("Order processed: " + OrderID + ", Customer: " + customer.getCustomerName() + ", Total Amount: " + TotalAmount);
    }
    }

    static void showProducts(){
        for(Product product:products){
            System.out.println("Product [ProductID=" + product.getProductID() + ", ProductName=" + product.getProductName() + ", Category=" + product.getCategory()
                    + ", Price=" + product.getPrice() + ", Description=" + product.getDescription() + ", Available=" + product.getAvailable() + ", farmer="
                    + product.getFarmer() + "]");
        }
    }

    static void showPaymentHistory(){
        for(Payment payment: payments){
            System.out.println(
                "Payment [PaymentID=" + payment.getPaymentID() + ", order=" + payment.getOrder() + ", PaymentStatus=" + payment.getPaymentStatus()
                    + ", PaymentDate=" + payment.getPaymentDate() + "]"
            );
        }
    }

    static void showOrders(){
        for(int i=0;i<orderMap.size();i++){
            System.out.println(
                orderMap.get(i)+" "
            );
        }
    }

    public static void main(String[] args) {
        do{
            System.out.println("-----Menu------");
            System.out.println("1.Add Customer");
            System.out.println("2.Add Farmer");
            System.out.println("3.Add Product");
            System.out.println("4.Show Products");
            System.out.println("5.Show Payments");
            System.out.println("6.Show Orders");
            System.out.println("7.Book Order");
            System.out.println("8.Exit");
            System.out.println();
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter CustomerID: ");
                    int customerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter CustomerName: ");
                    String CustomerName = sc.nextLine();
                    System.out.println("Enter Email: ");
                    String Email = sc.nextLine();
                    System.out.println("Enter Phone: ");
                    double Phone = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Enter Address: ");
                    String Address = sc.nextLine();
                    createCustomer(new Customer(customerID, CustomerName, Email, Phone, Address));
                    break;
                case 2:
                    System.out.println("Enter FarmerID: ");
                    int farmerID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter FarmerName: ");
                    String FarmerName = sc.nextLine();
                    System.out.println("Enter Phone: ");
                    double phoneNumber = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Enter Address: ");
                    String farmerAddress = sc.nextLine();
                    System.out.println("Enter BankDetails: ");
                    String BankDetails = sc.nextLine();
                    createFarmer(new Farmer(farmerID, FarmerName, phoneNumber, farmerAddress, BankDetails));
                    break;
                case 3:
                    System.out.println("Enter ProductID: ");
                    int productID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter Name: ");
                    String productName = sc.nextLine();
                    System.out.println("Category: ");
                    String Category = sc.nextLine();
                    System.out.println("Price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Description: ");
                    String Description = sc.nextLine();
                    System.out.println("Availability: ");
                    boolean Availability = sc.nextBoolean();
                    addProduct(new Product(productID, productName, Category, price, Description, Availability, getFarmerByID()));
                    break;
                case 4:
                    showProducts();
                    break;
                case 5:
                    showPaymentHistory();
                    break;
                case 6:
                    showOrders();
                    break;
                case 7:
                    isOrder(getFarmerID(), getProductID());
                    break;
                case 8:
                    sc.close();
                    return;
                default:

                    break;
            }
        }while(true);
    }
}