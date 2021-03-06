package com.lt.core.controller;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lt.common.Constants;
import com.lt.common.ResponseUtils;
import com.lt.common.session.SessionProvider;
import com.lt.core.bean.Product;
import com.lt.core.bean.User;
import com.lt.core.service.product.ProductService;
import com.lt.core.service.user.UserService;



@Controller
public class CommonController extends HttpServlet{
	
	@Autowired
	private SessionProvider sessionProvider;
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;
	
	//首页
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest request,ModelMap model){
		//加载用户
		User user = (User) sessionProvider.getAttribute(request, Constants.UER_SESSION);
		if(user!=null){
			Integer type=user.getUserType();
			model.addAttribute("listType", type);
			model.addAttribute("user", user);
			List<Product> productList=productService.getProductList();
			model.put("productList", productList);
			return "index";
		}else{
			List<Product> productList=productService.getProductList();
			model.put("productList", productList);
			return "index";
		}

	}
	//登录页
	@RequestMapping(value = "/login.do")
	public String login(){
		return "login";
	}
	
	
	//查看页
	@RequestMapping(value = "/show.do")
	public String show(HttpServletRequest request,Integer id,ModelMap model){
		User user = (User) sessionProvider.getAttribute(request, Constants.UER_SESSION);
		if(user!=null){
			model.addAttribute("user", user);
			Product product=productService.show(id);
			model.addAttribute("product", product);
			return "show";
		}else{
			Product product=productService.show(id);
			model.addAttribute("product", product);
			return "show";
		}

	}
	//确认登录
	@RequestMapping(value = "/api/login.do")
	public void login (HttpServletRequest request,HttpServletResponse response,ModelMap model){
			String userName=request.getParameter("userName");
			String password=request.getParameter("password");
			User user=userService.getUserByUsername(userName);
			if(user.getPassword().equals(password)){
			//把用户对象放在Session
			sessionProvider.setAttribute(request, Constants.UER_SESSION, user);
			JSONObject jo = new JSONObject();
			int code=200;
			jo.put("code",code);
			jo.put("message", "登录成功");
			jo.put("result", true);
			ResponseUtils.renderJson(response, jo.toString());
			}else{
				JSONObject jo = new JSONObject();
				int code=404;
				jo.put("code",code);
				jo.put("message", "密码错误");
				jo.put("result", false);
				ResponseUtils.renderJson(response, jo.toString());
			}

	}
	//退出
	@RequestMapping(value = "/logout.do")
	public String logout(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		User user = (User) sessionProvider.getAttribute(request, Constants.UER_SESSION);
		sessionProvider.logout(request, response);
		return "redirect:/login.do";
	}
	
	
}
