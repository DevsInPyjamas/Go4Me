package com.go4me.prototype.controller;


import com.go4me.prototype.model.AdsOrderService;
import com.go4me.prototype.model.OrderRequest;
import com.go4me.prototype.model.OrderService;
import com.go4me.prototype.model.User;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class OrderRequestController {
    @Autowired
    OrderService orderService;

    @Autowired
    AdsOrderService adsOrderService;

    @GetMapping("/search")
    public String searchOrderRequests(@RequestParam(name="lat", required=true) double latitude,
     @RequestParam(name="long", required=true) double longitude, Model model){
        model.addAttribute("AdsOrder", adsOrderService.getAll());
        return "searchorder";
    }

    @GetMapping("/neworder")
    public String newOrderView(OrderRequest order, Model model){
        model.addAttribute("newOrder", order);
        return "neworderview";
    }

    @GetMapping("/order/{id}")
    public String orderPanel(@PathVariable("id") Long id, Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrderRequest order = orderService.getOrderById(id);
        model.addAttribute("isMyOrder", order.getPublishedBy().getUserName().equals(user.getUserName()));
        model.addAttribute("Order", orderService.getOrderById(id));
        return "orderpanel";
    }

    @PostMapping("/order/7")
    public String lifeHacks(Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrderRequest or = new OrderRequest(user, "toString por defecto",
                15, 15, "May", "17:40");
        orderService.add(or);
        return orderPanel(or.getID(), model);
    }

    @PostMapping("/neworder")
    public RedirectView registerNewUser(@Valid OrderRequest neworder, BindingResult result, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        neworder.setPublishedBy(user);
        orderService.add(neworder);
        model.addAttribute("create", true);
        return new RedirectView("/order/" + neworder.getID());
    }

}
