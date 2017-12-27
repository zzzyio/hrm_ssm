package org.deepsl.hrm.controller;

import org.apache.commons.io.FileUtils;
import org.deepsl.hrm.domain.Document;
import org.deepsl.hrm.domain.User;
import org.deepsl.hrm.service.DocumentService;
import org.deepsl.hrm.util.common.HrmConstants;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    /**
     * 增加document
     * @param flag
     * @return
     */
    @RequestMapping("addDocument")
    public String preAddDocument(Integer flag)
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
            String fileName = UUID.randomUUID().toString().replace("-","") + "_" + file.getOriginalFilename();
            //获取保存文件的服务器的绝对路径
            String documentUploadDir = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");
            //把临时文件保存到指定的路径
            File file1 = new File(documentUploadDir, fileName);
            //获取到新文件名，为之后保存到持久层提供数据。
            document.setFileName(fileName);
            //上传的时间
            document.setCreateDate(new Date());
            //上传的用户
            User user = (User) request.getSession().getAttribute(HrmConstants.USER_SESSION);
            document.setUser(user);

            try
            {
                //实际上传的文件转移到指定的路径
                file.transferTo(file1);

                //文件的相关信息保存到持久层
                documentService.addDocument(document);
            } catch (IOException e)
            {
                e.printStackTrace();
                return "404";
            }

        }
        return "forward:selectDocument";
    }

    /**
     * 更新document
     * @param flag
     * @param id
     * @param model
     * @return
     */
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

    /**
     * 删除document
     * @param ids
     * @param request
     * @param pageModel
     * @param model
     * @return
     */
    @RequestMapping("removeDocument")
    public String removeDocument(Integer[] ids, HttpServletRequest request, PageModel pageModel, Model model)
    {
        //连文件一起删除，让service干活，传个realPath给它即可
        String realPath = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");

        documentService.removeDocumentById(ids, realPath);

        model.addAttribute("pageMoel", pageModel);

        return "forward:selectDocument";
    }

    /**
     * 下载document
     * @param request
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("downLoad")
    public ResponseEntity<byte[]> downLoad(HttpServletRequest request, Integer id) throws IOException
    {
        if (id != null)
        {
            Document documentById = documentService.findDocumentById(id);
            //文件名
            String fileName = documentById.getFileName();
            //标题名
            String title = documentById.getTitle();

            //去哪里下载？获取到文件的保存路径
            String realPath = request.getServletContext().getRealPath("WEB-INF/DocumentUpload");
            File file = new File(realPath + File.separator + fileName);

            HttpHeaders headers = new HttpHeaders();

            //下载显示的文件名，解决中文名称乱码问题。这里使用title名为文件名。不想让用户看到保存到数据库中的文件名的uuid一长串。
            String substring = fileName.substring(fileName.lastIndexOf(".") + 1);//截取文件后缀名
            String titleName = title + "." + substring;//组合成新的下载名。
            String downloadFielName = new String(titleName.getBytes("UTF-8"), "iso-8859-1");//解决中文名乱码

            //通知浏览器以attachment（下载方式）打开图片
            headers.setContentDispositionFormData("attachment", downloadFielName);

            //application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        }
        return null;//这里应该人性化地返回一个异常页面……
    }

    /**
     * 查找document，查询结果分页
     * @param document
     * @param request
     * @param pageModel
     * @param model
     * @return
     */
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
