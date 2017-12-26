package org.deepsl.hrm.controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.deepsl.hrm.dao.DocumentDao;
import org.deepsl.hrm.domain.Document;
import org.deepsl.hrm.domain.User;
import org.deepsl.hrm.service.DocumentService;
import org.deepsl.hrm.service.HrmService;
import org.deepsl.hrm.util.common.HrmConstants;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version V1.0
 * @Description: 处理上传下载文件请求控制器
 */

@Controller
@RequestMapping("document")
public class DocumentController
{
    @Autowired
    DocumentService documentService;

    //=======================增加document==============================
    @RequestMapping("addDocument")
    public String preAddDocument(int flag)
    {
        if (flag == 1)
        {
            return "document/showAddDocument";
        }
        else if (flag == 2)
        {
            return "forward:saveDocument";
        }
        return "";//转到错误的页面，待填
    }

    @RequestMapping("saveDocument")
    public String saveDocument(Document document, MultipartFile file, HttpServletRequest request)
    {
        if (!file.isEmpty())
        {
            //得到文件上传的数据，保存到指定的file，用uuid随机生成一个数，加上原文件名作为新的文件名保存到数据库。
            String fileName = UUID.randomUUID().toString().replace("-","") + file.getOriginalFilename();
            //获取保存文件的服务器的绝对路径
            String documentUploadDir = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");
            //把临时文件保存到指定的路径
            File file1 = new File(documentUploadDir, fileName);
            //获取到新文件名，为之后保存到持久层提供数据。
            document.setFileName(fileName);
            //上传的时间
            document.setCreateDate(new Date());
            //上传的用户id是……

            try
            {
                //实际上传的文件转移到指定的路径
                file.transferTo(file1);

                //文件的相关信息保存到持久层
                documentService.addDocument(document);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return "forward:selectDocument";
    }
    //=======================更新document==============================
    @RequestMapping("updateDocument")
    public String preUpdateDocument(int flag, int id, Model model)
    {
        if (flag == 1 && id > 0)
        {
            Document document = documentService.findDocumentById(id);
            model.addAttribute("document", document);
            return "document/showUpdateDocument";
        }
        else if (flag == 2)
        {
            return "forward:modifyDocument";
        }
        return "";//转到错误的页面，待填
    }
    @RequestMapping("modifyDocument")
    public String modifyDocument(Document document, MultipartFile file, HttpServletRequest request)
    {
        //这里面临一个问题，就是用户更不更新file
        // 更新的话，那上传的文件就不是空
        if (!file.isEmpty())
        {
            //得到文件上传的数据，保存到指定的file，用uuid随机生成一个数，加上原文件名作为新的文件名保存到数据库。
            String fileName = UUID.randomUUID().toString().replace("-","") + file.getOriginalFilename();
            //获取保存文件的服务器的绝对路径
            String documentUploadDir = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");
            //把临时文件保存到指定的路径
            File file1 = new File(documentUploadDir, fileName);
            //获取到新文件名，为之后保存到持久层提供数据。
            document.setFileName(fileName);
            //一起更新文件上传的时间
            document.setCreateDate(new Date());

            try
            {
                //实际上传的文件转移到指定的路径
                file.transferTo(file1);
                //文件的相关信息保存到持久层
                documentService.modifyDocument(document);

                return "forward:selectDocument";
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //不更新file的话，file为空，直接先查一遍数据库，得到filename，和createTime
        Document documentById = documentService.findDocumentById(document.getId());
        String fileName = documentById.getFileName();
        Date createDate = documentById.getCreateDate();

        document.setFileName(fileName);
        document.setCreateDate(createDate);

        documentService.modifyDocument(document);

        return "forward:selectDocument";
    }


    //=======================删除document==============================
    @RequestMapping("removeDocument")
    public String removeDocument(Integer[] ids, HttpServletRequest request, PageModel pageModel, Model model)
    {
        //连文件一起删除，让service干活，传个realPath给它即可
        String realPath = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");

        documentService.removeDocumentById(ids, realPath);

        model.addAttribute("pageMoel", pageModel);

        return "forward:selectDocument";
    }

    //=======================下载document==============================
    @RequestMapping("downLoad")
    public ResponseEntity<byte[]> downLoad(HttpServletRequest request, String fileName) throws IOException
    {
        //去哪里下载？获取到文件的保存路径
        String realPath = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");
        File file = new File(realPath + File.separator + fileName);

        HttpHeaders headers = new HttpHeaders();

        //下载显示的文件名，解决中文名称乱码问题
        String downloadFielName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");

        //通知浏览器以attachment（下载方式）打开图片
        headers.setContentDispositionFormData("attachment", downloadFielName);

        //application/octet-stream ： 二进制流数据（最常见的文件下载）。
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }

    //=======================查找document，查询结果分页==============================
    @RequestMapping("selectDocument")
    public String selectDocument(Document document, HttpServletRequest request, PageModel pageModel, Model model)
    {
        String parameter = request.getParameter("pageModel.pageIndex");
        if (parameter != null)
        {
            int pageIndex = Integer.parseInt(parameter);
            pageModel.setPageIndex(pageIndex);
        }

        List<Document> documents = documentService.findDocument(document, pageModel);

        model.addAttribute("documents", documents);
        return "document/document";
    }

}
