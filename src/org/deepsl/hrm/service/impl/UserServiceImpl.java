package org.deepsl.hrm.service.impl;

import org.deepsl.hrm.dao.UserDao;
import org.deepsl.hrm.domain.User;
import org.deepsl.hrm.service.UserService;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static org.deepsl.hrm.util.common.HrmConstants.PAGE_DEFAULT_SIZE;

/**
 * Author guopop
 * Description
 * Date created in 2017/12/25 21:40
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserDao userDao;

    /**
     * HrmServiceImpl接口login方法实现
     *  @see { HrmService }
     * */
    @Transactional(readOnly=true)
    @Override
    public User login(String loginname, String password) {
        System.out.println("HrmServiceImpl login -- >>");
        HashMap<String, String> hashMap= new HashMap<>();
        hashMap.put("loginname",  loginname );
        hashMap.put("password",  password );
        return userDao.selectByLoginnameAndPassword(hashMap);
    }
    /**
     * HrmServiceImpl接口findUser方法实现
     * @see { HrmService }
     * */
    @Transactional(readOnly=true)
    @Override
    public List<User> findUser(User user, PageModel pageModel) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user", user);
        Integer count = userDao.count(hashMap);
        pageModel.setRecordCount(count);

        hashMap.put("pageModel", pageModel);

        List<User> users = userDao.selectByPage(hashMap);

        return users;
    }

    /**
     * HrmServiceImpl接口findUserById方法实现
     * @see { HrmService }
     * */
    @Transactional(readOnly=true)
    @Override
    public User findUserById(Integer id) {
        return userDao.selectById(id);
    }

    /**
     * HrmServiceImpl接口removeUserById方法实现
     * @see { HrmService }
     * */
    @Transactional
    @Override
    public void removeUserById(Integer id) {
        userDao.deleteById(id);
    }

    /**
     * HrmServiceImpl接口modifyUser方法实现
     * @see { HrmService }
     * */
    @Transactional
    @Override
    public void modifyUser(User user) {
        userDao.update(user);
    }

    /**
     * HrmServiceImpl接口addUser方法实现
     * @see { HrmService }
     * */
    @Transactional
    @Override
    public void addUser(User user) {
        userDao.save(user);
    }
}
