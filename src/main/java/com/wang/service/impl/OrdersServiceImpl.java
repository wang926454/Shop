package com.wang.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wang.mapper.OrderItemMapper;
import com.wang.mapper.OrdersMapper;
import com.wang.mapper.UserShopMapper;
import com.wang.model.OrderItem;
import com.wang.model.Orders;
import com.wang.model.UserShop;
import com.wang.service.IOrdersService;


/**
 * 订单
 * @author wang926454
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
	
	@Autowired
	private UserShopMapper userShopMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private OrdersMapper ordersMapper;

	@Override
	public boolean addOrders(Orders orders) {
		// 将购物车商品添加到该订单下
		Map map = new HashMap(16);
		map.put("userId", orders.getUserId());
		List<UserShop> userShops = this.userShopMapper.findUserShopList(map);
		// 判断购物车有没有商品
		if(userShops.size() == 0){
			return false;
		}
		// 新增订单
		ordersMapper.insert(orders);
		for(int i = 0; i < userShops.size(); i++){
			UserShop userShop = userShops.get(i);
			OrderItem orderItem = new OrderItem();
			orderItem.setCount(userShop.getCount());
			orderItem.setPrice(userShop.getPrice());
			
			//orderItem.setItemId(userShop.getItemId()); // 不理解为什么关联查询了，就是不能直接查到外键？xml里有这个列啊，为什么是null
			// 一定要这样可以获取到值。。。
			orderItem.setItemId(userShop.getItem().getId());
			orderItem.setOrdersId(orders.getId());
			orderItemMapper.insert(orderItem);
			userShopMapper.deleteById(userShop.getId());
		}
		return true;
	}

	@Override
	public Page<Orders> findOrdersList(Page<Orders> page, Map map) {
	    return page.setRecords(ordersMapper.findOrdersList(page, map));
	}

	@Override
	public Page<Orders> findOrdersListAdmin(Page<Orders> page, Map map) {
	    return page.setRecords(ordersMapper.findOrdersListAdmin(page, map));
	}
}
