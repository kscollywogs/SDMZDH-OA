package com.zdh.web.servlet;

import com.zdh.domain.Project;
import com.zdh.domain.User;
import com.zdh.service.ProjectService;
import com.zdh.service.UserService;
import com.zdh.utils.CommonsUtils;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class AddProjectServlet extends BaseServlet {

    UserService userservice = new UserService();
    ProjectService projectService = new ProjectService();

    public void addProject(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {

        //取得项目
        Map<String,String[]> map =  request.getParameterMap();
        Project project = new Project();
        BeanUtils.populate(project,map);
        project.setId(CommonsUtils.getUUID());

        //为人员加入项目,并获得人员名字。
        int bce = 1;
        int bmanager = 1;
        int bdesigner = 1;
        int breviewer = 1;
        int boffice = 1;
        if(project.getCe()!="" && project.getCe()!=null){
            User uce = userservice.getUserByName(project.getCe());
            if(uce != null){
                bce = projectService.joinProject(uce.getUsername(),project.getId());
            }
        }
        if(project.getManager()!="" && project.getManager()!=null){
            bmanager = projectService.joinProject(project.getManager(),project.getId());
            String manager = userservice.getUserByUsername(project.getManager()).getName();
            project.setManager(manager);
        }
        if(project.getDesigner()!="" && project.getDesigner()!=null){
            bdesigner = projectService.joinProject(project.getDesigner(),project.getId());
            String designer = userservice.getUserByUsername(project.getDesigner()).getName();
            project.setDesigner(designer);
        }
        if(project.getReviewer()!="" && project.getReviewer()!=null){
            breviewer = projectService.joinProject(project.getReviewer(),project.getId());
            String reviewer = userservice.getUserByUsername(project.getReviewer()).getName();
            project.setReviewer(reviewer);
        }
        if(project.getOffice()!="" && project.getOffice()!=null){
            boffice = projectService.joinProject(project.getOffice(),project.getId());
            String office = userservice.getUserByUsername(project.getOffice()).getName();
            project.setOffice(office);
        }

        //向数据库存入项目信息
        int r = projectService.addProject(project);

        PrintWriter out = response.getWriter();
        if(r>0 && bce>0 && bmanager>0 && bdesigner>0 && breviewer>0 && boffice>0){
            out.print("<script>alert('提交成功！');window.location='"+request.getContextPath()+"/projectServlet?method=getAllProject';</script>");
        }else{
            out.print("<script>alert('提交失败！');window.location='"+request.getContextPath()+"/addProjectServlet?method=getPeopleInfo';</script>");
        }
    }


    public void getPeopleInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {
        //取得人员列表
        List<User> list = userservice.findAllUser();
        list.remove(0);
        request.setAttribute("userList",list);
        request.getRequestDispatcher("/dist/forms/project.jsp").forward(request,response);
    }



}
