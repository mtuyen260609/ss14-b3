# BÀI TẬP 3: THIẾT KẾ VŨ ĐIỆU CHOREOGRAPHY SAGA

## a) Phân tích I/O
- **Input:**
  - Thông tin khách hàng (customerId, address, ...).
  - Ví tiền (balance).
  - Tồn kho (số lượng sản phẩm, productId).
- **Output:**
  - Trạng thái cuối cùng của Order Service: \COMPLETED\ (thành công) hoặc \CANCELLED\ (thất bại).
  - Trạng thái cuối cùng của Payment Service: \PAID\ (thành công) hoặc \REFUNDED\ (hoàn tiền).
  - Trạng thái cuối cùng của Shipping Service: \SHIPPED\ (thành công) hoặc \FAILED\ (thất bại).

## b) Luồng xử lý thành công
1. **Order Service**: Tạo đơn hàng với trạng thái \PENDING\ -> Gửi sự kiện \OrderCreatedEvent\.
2. **Payment Service**: Lắng nghe \OrderCreatedEvent\ -> Trừ tiền ví khách hàng -> Gửi sự kiện \PaymentSuccessEvent\.
3. **Shipping Service**: Lắng nghe \PaymentSuccessEvent\ -> Kiểm tra địa chỉ và tạo vận đơn -> Gửi sự kiện \ShippingSuccessEvent\.
4. **Order Service**: Lắng nghe \ShippingSuccessEvent\ -> Cập nhật trạng thái đơn hàng thành \COMPLETED\.

## c) Luồng bù trừ (Compensation) khi Shipping thất bại
1. **Shipping Service**: Gặp lỗi (ví dụ địa chỉ không hợp lệ) -> Gửi sự kiện \ShippingFailedEvent\.
2. **Order Service**: Lắng nghe \ShippingFailedEvent\ -> Gửi sự kiện yêu cầu hoàn tiền \CompensatePaymentEvent\.
3. **Payment Service**: Lắng nghe \CompensatePaymentEvent\ -> Hoàn tiền (Refund) -> Gửi sự kiện \RefundSuccessEvent\.
4. **Order Service**: Lắng nghe \RefundSuccessEvent\ -> Cập nhật trạng thái đơn hàng thành \CANCELLED\.

## d) Xử lý tình huống Shipping Service bị timeout
- **Cơ chế**: Khi Order Service phát ra \PaymentSuccessEvent\ (hoặc OrderCreated), nó sẽ lưu một job kiểm tra timeout (ví dụ: dùng Quartz, hoặc Timeout pattern trong Saga).
- Nếu sau **30 giây** không nhận được phản hồi (\ShippingSuccessEvent\ hoặc \ShippingFailedEvent\) từ Shipping Service:
  - **Order Service** tự động đánh dấu đơn hàng là \TIMEOUT_FAILED\.
  - **Order Service** tự động kích hoạt luồng bù trừ bằng cách gửi \CompensatePaymentEvent\ để Payment Service hoàn tiền.

## e) Lưu đồ (Flowchart)
\\\mermaid
sequenceDiagram
    participant O as Order Service
    participant P as Payment Service
    participant S as Shipping Service

    Note over O,S: LUỒNG THÀNH CÔNG
    O->>O: Tạo Order (PENDING)
    O-)P: OrderCreatedEvent
    P->>P: Trừ tiền thành công
    P-)S: PaymentSuccessEvent
    S->>S: Tạo vận đơn thành công
    S-)O: ShippingSuccessEvent
    O->>O: Cập nhật Order (COMPLETED)

    Note over O,S: LUỒNG THẤT BẠI (BÙ TRỪ)
    O->>O: Tạo Order (PENDING)
    O-)P: OrderCreatedEvent
    P->>P: Trừ tiền thành công
    P-)S: PaymentSuccessEvent
    S->>S: Lỗi địa chỉ
    S-)O: ShippingFailedEvent
    O-)P: CompensatePaymentEvent
    P->>P: Hoàn tiền (Refund)
    P-)O: RefundSuccessEvent
    O->>O: Hủy Order (CANCELLED)
\\\
"@ -Encoding UTF8

Set-Content -Path "C:\Users\Admin\OneDrive\HRM\exercises\ss14-b3\saga-choreography\pom.xml" -Value @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>saga-choreography</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>saga-choreography</name>
    <description>Demo Choreography Saga</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
