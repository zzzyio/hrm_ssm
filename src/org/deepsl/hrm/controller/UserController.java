package org.deepsl.hrm.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deepsl.hrm.domain.User;
import org.deepsl.hrm.service.HrmService;
import org.deepsl.hrm.service.UserService;
import org.deepsl.hrm.util.common.HrmConstants;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 处理用户请求控制器
 * */
@Controller
public class UserController {
	
	/**
	 * 自动注入UserService
	 * */
	@Autowired
	private UserService userService;
		
	/**
	 * 处理登录请求
	 * @param String loginname  登录名
	 * @param String password 密码
	 * @return 跳转的视图
	 * */
	@RequestMapping("login")
	 public ModelAndView login(@RequestParam("loginname") String loginname,
			 @RequestParam("password") String password,
			 HttpSession session,
			 ModelAndView mv){
		// 调用业务逻辑组件判断用户是否可以登录
		User user = userService.login(loginname, password);
		if(user != null){
			// 将用户保存到HttpSession当中
			session.setAttribute(HrmConstants.USER_SESSION, user);
			// 客户端跳转到main页面
			mv.setViewName("redirect:/main");
		}else{
			// 设置登录失败提示信息
			mv.addObject("message", "登录名或密码错误!请重新输入");
			// 服务器内部跳转到登录页面
			mv.setViewName("forward:/loginForm");
		}
		return mv;
	}

	@RequestMapping("logout.action")
    public String logout(HttpServletRequest request) {
	    request.getSession().invalidate();
	    return "loginForm";
    }


	@RequestMapping("user/preAddUser")
	public String preAddUser() {
	    return "user/showAddUser";
    }

    @RequestMapping("user/addUser")
    public String addUser(User user) {
	    Date date = new Date();
	    user.setCreateDate(date);
	    userService.addUser(user);
	    return "redirect:/user/selectUser";
    }

	@RequestMapping("user/selectUser")
	public String selectUser(User user, PageModel pageModel, Model model) {
        List<User> users = userService.findUser(user, pageModel);
        model.addAttribute("users", users);
        model.addAttribute("pageModel", pageModel);
        return "user/user";
    }

    @RequestMapping("user/updateUser")
    public String updateUser(String flag, User user, Model model) {
	    if ("1".equals(flag)) {
            User userById = userService.findUserById(user.getId());
            model.addAttribute("user", userById);
	        return "/user/showUpdateUser";
        }
        if ("2".equals(flag)) {
            userService.modifyUser(user);
	        return "redirect:/user/selectUser";
        }
        return "404.html";
    }

    @RequestMapping("user/removeUser")
    public String removeUser(String[] ids) {
        List<String> idsStr = Arrays.asList(ids);

        for (String id: idsStr) {
            int i = Integer.parseInt(id);
            userService.removeUserById(i);
        }
        return "redirect:/user/selectUser";
    }
}
