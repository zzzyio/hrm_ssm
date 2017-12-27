package org.deepsl.hrm.service.impl;

import org.deepsl.hrm.dao.DocumentDao;
import org.deepsl.hrm.domain.Document;
import org.deepsl.hrm.service.DocumentService;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService
{
    //spring自动注入持久层documentDao对象（要在配置文件中配置代理对象的生成）
    @Autowired
    private DocumentDao documentDao;

    @Transactional(readOnly = true)
    @Override
    public List<Document> findDocument(Document document, PageModel pageModel)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        if (pageModel != null)
        {
            //分页的条目数，已经默认为4，可以自己在这里更改。但是要大于4才能成功，因为原get方法的条件限制了。当然也可以直接在原来的方法那里改。
            pageModel.setPageSize(5);
            //从数据库获得总的条目数
            pageModel.setRecordCount(documentDao.count());
            //分页的偏移
            int firstLimitParam = pageModel.getFirstLimitParam();

            hashMap.put("document", document);
            hashMap.put("offset", firstLimitParam);
            hashMap.put("limit", pageModel.getPageSize());
        }

        return documentDao.selectByPage(hashMap);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void addDocument(Document document)
    {
        documentDao.save(document);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void modifyDocument(Document document)
    {
        documentDao.update(document);
    }

    @Transactional(readOnly = true)
    @Override
    public Document findDocumentById(Integer id)
    {
        return documentDao.selectById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void removeDocumentById(Integer[] ids, String realPath)
    {
        for (int i = 0; i < ids.length; i++)
        {
            //删除该条目之前，先找出它存储的文件名，删除了在disk上的文件再删除它
            Document documentById = findDocumentById(ids[i]);
            String fileName = documentById.getFileName();
            File file = new File(realPath, fileName);
            file.delete();

            //接着删除条目
            documentDao.deleteById(ids[i]);
        }
    }
}
