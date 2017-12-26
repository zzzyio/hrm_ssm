package org.deepsl.hrm.service.impl;

import org.deepsl.hrm.dao.DeptDao;
import org.deepsl.hrm.domain.Dept;
import org.deepsl.hrm.service.DeptService;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Transactional
@Service
public class DeptServiceImpl implements DeptService{

    /*****************部门服务接口实现*************************************/
    @Autowired
    private DeptDao deptDao;


    @Override
    public List<Dept> findDept(Dept dept, PageModel pageModel) {


        //查符合名字count的个数
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("name",dept.getName());
        Integer recordCount = deptDao.count(map1);
        pageModel.setRecordCount(recordCount);

        List<Dept> depts =null;
        if(recordCount==0){
            //count=0 直接depts 为null
        }else{
            //获取当前页数 recordCount --> totalsize --> pageindex 这样pageModel就被填满了
            int pageIndex = pageModel.getPageIndex();
            int pageSize = pageModel.getPageSize();
            int offset = (pageIndex - 1) * pageSize;
            map1.put("limit",pageSize);
            map1.put("offset",offset);
            depts = deptDao.selectByPage(map1);
        }

        return depts;
    }

    @Override
    public List<Dept> findAllDept() {
        HashMap<String, Object> map1 = new HashMap<>();
        PageModel pageModel = new PageModel();
        int pageSize = pageModel.getPageSize();
        map1.put("limit",pageSize);
        map1.put("offset",0);

        List<Dept> depts = deptDao.selectByPage(map1);
        return depts;
    }

    @Transactional
    @Override
    public void removeDeptById(String ids) {
        String[] split = ids.split(",");
        ArrayList<Integer> integers = new ArrayList<>();
        for (String x : split ) {
            Integer integer = Integer.valueOf(x);
            integers.add(integer);
        }
        deptDao.deleteById(integers);
    }

    @Transactional
    @Override
    public void addDept(Dept dept) {
        deptDao.save(dept);
    }

    @Override
    public Dept findDeptById(Integer id) {
        return deptDao.selectById(id);
    }
    @Transactional
    @Override
    public void modifyDept(Dept dept) {
        deptDao.update(dept);
    }
}
