package com.zdh.web.servlet;
import com.zdh.domain.User;
import com.zdh.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.DbUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserServlet extends BaseServlet{

    UserService service = new UserService();


    //用户登录
    public void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {
        HttpSession session = request.getSession();

        //获得输入的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        //将用户名和密码传递给service层
        User user = null;
        try {
            user = service.login(username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //判断用户是否登录成功 user是否是null
        if(user!=null){
            //将user对象存到session中
            session.setAttribute("user", user);
            //如果用户不是root
            if(user.getPower()!=0){
                //重定向到首页
                System.out.println("登陆成功"+user.getUsername()+""+user.getName());
                response.sendRedirect(request.getContextPath()+"/index.jsp");
            }else{
                //重定向到root页面
                System.out.println("登陆成功"+user.getUsername()+""+user.getName());
                response.sendRedirect(request.getContextPath()+"/dist/root/register.jsp");
            }


        }else{
            request.setAttribute("loginError", "用户名或密码错误");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }


    //修改密码
    public void updatePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {
        //获得输入的用户名和密码
        String username = request.getParameter("username");
        String oldPIN = request.getParameter("oldPIN");
        String newPIN = request.getParameter("newPIN");
        String newPIN2 = request.getParameter("newPIN2");

        //验证两次密码是否相同
        if(!newPIN.equals(newPIN2)){
            request.setAttribute("loginError", "两次新密码不一致");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }else {
            //验证user是否存在且匹配
            User user = null;
            try {
                user = service.login(username,oldPIN);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(user == null){
                request.setAttribute("loginError", "用户名或旧密码错误");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }else{
                //修改密码
                int i = service.updatePassword(username,newPIN);
                PrintWriter out = response.getWriter();
                if(i>0){
                    out.print("<script>alert('密码修改成功！');window.location='"+request.getContextPath()+"/login.jsp';</script>");
                }else{
                    out.print("<script>alert('密码修改失败！');window.location='"+request.getContextPath()+"/login.jsp';</script>");
                }
            }
        }
    }


    //用户注销
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvocationTargetException, IllegalAccessException {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        response.sendRedirect(request.getContextPath()+"/login.jsp");
    }


}
