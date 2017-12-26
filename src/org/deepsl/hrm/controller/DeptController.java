package org.deepsl.hrm.controller;

import org.deepsl.hrm.dao.DeptDao;
import org.deepsl.hrm.domain.Dept;
import org.deepsl.hrm.service.DeptService;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**   
 * @Description: 处理部门请求控制器
 * @author   
 * @version V1.0   
 */

@RequestMapping("dept")
@Controller
public class DeptController {
    @Autowired
    DeptService deptService;

    @Autowired
    DeptDao deptDao;

    @RequestMapping("selectDept")
    public String selectDept(Dept dept ,PageModel pageModel, HttpServletRequest request){

        /*这里的方法参数列表里的对象都不为空*/


        List<Dept> depts = deptService.findDept(dept, pageModel);
        request.setAttribute("depts",depts);
        request.setAttribute("pageModel",pageModel);


        return "dept/dept";
    }
    @RequestMapping("updateDept")
    public String updateDept(Dept dept,int flag,Model model){
        if(flag==1){
            Dept deptById = deptService.findDeptById(dept.getId());
            model.addAttribute("dept",deptById);
            return "dept/showUpdateDept";
        }

        deptService.modifyDept(dept);
        //这里从一个方法到另一个方法 避免自定义的视图解析器加上/WEB-INF/jsp 所以用重定向
        return "redirect:selectDept";
    }

    @RequestMapping("addDept")
    public String addDept(Dept dept,int flag){
        if(flag==1){
        return "dept/showAddDept";
        }
        deptService.addDept(dept);
        return "redirect:selectDept";
    }


    @RequestMapping("removeDept")
    public String removeDept(String ids){

        System.out.println("DeptController.removeDept"+ids);
        deptService.removeDeptById(ids);
        return "redirect:selectDept";

    }
}
