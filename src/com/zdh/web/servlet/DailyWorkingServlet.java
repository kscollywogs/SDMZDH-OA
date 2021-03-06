package com.zdh.web.servlet;

import com.zdh.domain.*;
import com.zdh.service.DailyWorkingService;
import com.zdh.service.DebugWorkingService;
import com.zdh.service.DesignWorkingService;
import com.zdh.service.ProjectService;
import com.zdh.utils.CommonsUtils;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class DailyWorkingServlet extends BaseServlet {

    DailyWorkingService dailyWorkingService = new DailyWorkingService();


    public void addDailyWorking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {

        //取得项目
        Map<String,String[]> map =  request.getParameterMap();
        DailyWorking dailyWorking = new DailyWorking();
        BeanUtils.populate(dailyWorking,map);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        dailyWorking.setId(CommonsUtils.getUUID());
        dailyWorking.setUsername(user.getUsername());
        dailyWorking.setYear(CommonsUtils.getCurrentYear());
        dailyWorking.setMonth(CommonsUtils.getCurrentMonth());

        //向数据库存入项目信息

        int r = dailyWorkingService.addDailyWorking(dailyWorking);

        PrintWriter out = response.getWriter();
        if(r>0){
            out.print("<script>alert('提交成功！');window.location='"+request.getContextPath()+"/dist/forms/daily.jsp';</script>");
        }else{
            out.print("<script>alert('提交失败！');window.location='"+request.getContextPath()+"/dist/forms/daily.jsp';</script>");
        }
    }

    public void updateDailyWorking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {

        //取得项目
        Map<String,String[]> map =  request.getParameterMap();
        DailyWorking dailyWorking = new DailyWorking();
        BeanUtils.populate(dailyWorking,map);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        dailyWorking.setUsername(user.getUsername());

        //向数据库存入项目信息
        int r = dailyWorkingService.updateDailyWorking(dailyWorking);

        PrintWriter out = response.getWriter();
        if(r>0){
            out.print("<script>alert('修改成功！');window.location='"+request.getContextPath()+"/personalSummaryServlet?method=getAllWorkingList';</script>");
        }else{
            out.print("<script>alert('修改失败！');window.location='"+request.getContextPath()+"/personalSummaryServlet?method=getAllWorkingList';</script>");
        }
    }

    //通过id得到
    public void getDailyWorkingInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {


        //通过id得到对象
        String dailyid = request.getParameter("dailyid");
        DailyWorking dailyWorking = dailyWorkingService.getDailyWorkingInfo(dailyid);

        request.setAttribute("daily",dailyWorking);
        request.getRequestDispatcher("/dist/forms/daily1.jsp").forward(request, response);
    }

    //通过id删除日常工作
    public void deleteByid(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {


        //通过id得到对象
        String id = request.getParameter("dailyid");
        int r  = dailyWorkingService.deleteDailyWorkingByid(id);

        PrintWriter out = response.getWriter();
        if(r>0){
            out.print("<script>alert('已删除！');window.location='"+request.getContextPath()+"/personalSummaryServlet?method=getAllWorkingList';</script>");
        }else{
            out.print("<script>alert('删除失败！');window.location='"+request.getContextPath()+"/personalSummaryServlet?method=getAllWorkingList';</script>");
        }
    }
}
