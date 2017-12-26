package org.deepsl.hrm.service;

import org.deepsl.hrm.domain.Dept;
import org.deepsl.hrm.util.tag.PageModel;

import java.util.List;

public interface DeptService {
    /**
     * 获得所有部门，分页查询
     * @return Dept对象的List集合
     * */
    List<Dept> findDept(Dept dept, PageModel pageModel);

    /**
     * 获得所有部门
     * @return Dept对象的List集合
     * */
    List<Dept> findAllDept();

    /**
     * 根据id删除部门
     * @param id
     * */
    public void removeDeptById(String ids);

    /**
     * 添加部门
     * @param dept 部门对象
     * */
    void addDept(Dept dept);

    /**
     * 根据id查询部门
     * @param id
     * @return 部门对象
     * */
    Dept findDeptById(Integer id);

    /**
     * 修改部门
     * @param dept 部门对象
     * */
    void modifyDept(Dept dept);
}
