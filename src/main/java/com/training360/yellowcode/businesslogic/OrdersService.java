package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.database.OrdersDao;
import com.training360.yellowcode.database.ProductDao;
import com.training360.yellowcode.dbTables.OrderItem;
import com.training360.yellowcode.dbTables.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    private OrdersDao ordersDao;
    private ProductDao productDao;

    public static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public OrdersService(OrdersDao ordersDao, ProductDao productDao) {
        this.ordersDao = ordersDao;
        this.productDao = productDao;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Orders> listOrders() {
        return ordersDao.listOrders();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Orders> listActiveOrders() {
        return ordersDao.listActiveOrders();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderItem> listOrderItemsForAdmin(long orderId) {
        return ordersDao.listOrderItemsForAdmin(orderId);
    }

    public List<Orders> listOrdersByUserId(long userId) {
        return ordersDao.listOrdersByUserId(userId);
    }

    public List<OrderItem> listOrderItems(long userId, long orderId) {
        return ordersDao.listOrderItems(userId, orderId);
    }

    public void createOrderAndOrderItems(long userId, String address) {
        if (productDao.listProducts().size() != 0) {
            ordersDao.createOrderAndOrderItems(userId, address);
            List<Orders> orders = listOrdersByUserId(userId);
            List<OrderItem> orderItems = listOrderItems(userId, orders.get(orders.size() - 1).getId());
            LOGGER.info(MessageFormat.format("Order created(id: {0}, userId: {1}, date: {2}, status: {3})",
                    orders.get(orders.size() - 1).getId(), userId, LocalDateTime.now(), "ACTIVE"));
            for (OrderItem orderItem : orderItems) {
                LOGGER.info(MessageFormat.format("OrderItem added to order(id: {0}, orderId: {1}, productId: {2}, " +
                                "productName: {3}, producer: {4}, productPrice: {5})",
                        orderItem.getId(), orderItem.getOrderId(), orderItem.getProductId(),
                        orderItem.getProductName(), orderItem.getProducer(), orderItem.getProductPrice()));
            }
        }
        throw new IllegalStateException("Empty basket");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(long orderId) {
        ordersDao.deleteOrder(orderId);
        LOGGER.info(MessageFormat.format("Order (orderId:{0}) set to deleted", orderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrderItem(long orderId, String productAddress) {
        ordersDao.deleteOrderItem(orderId, productAddress);
        LOGGER.info(MessageFormat.format("Orderitem removed from (orderId:{0}) order", orderId));
        if (ordersDao.listOrderItemsForAdmin(orderId).size() == 0) {
            deleteOrder(orderId);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void modifyActiveStatusToDelivered(long orderId) {
        ordersDao.modifyActiveStatusToDelivered(orderId);
        LOGGER.info(MessageFormat.format("Order (orderId:{0}) status changed from active to delivered", orderId));
    }

    public List<String> listDeliveryAddressesOfUser(long userId) {
        return ordersDao.listDeliveryAddressesOfUser(userId);
    }
}
