package com.rufat.onlineshopping.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "total_price", nullable = false)
	private BigDecimal totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(name = "shipping_address", nullable = false, length = 500)
	private String shippingAddress;

	@Column(name = "customer_first_name", length = 100)
	private String customerFirstName;
	@Column(name = "customer_last_name", length = 100)
	private String customerLastName;
	@Column(name = "customer_phone", length = 30)
	private String customerPhone;
	@Column(name = "customer_email", length = 255)
	private String customerEmail;
	@Column(name = "payment_method", length = 20)
	private String paymentMethod;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<OrderItem> items;

	@Column(name = "cancellation_requested", nullable = false)
	@Builder.Default
	private boolean cancellationRequested = false;

	@Column(name = "cancellation_reason", length = 1000)
	private String cancellationReason;

	@Column(name = "cancellation_requested_at")
	private LocalDateTime cancellationRequestedAt;

	@PrePersist
	protected void onCreate() {
		if (this.status == null) {
			this.status = OrderStatus.PENDING;
		}
	}
}
