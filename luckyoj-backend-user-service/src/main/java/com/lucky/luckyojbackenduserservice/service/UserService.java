package com.lucky.luckyojbackenduserservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lucky.luckyojbackendmodel.model.dto.email.LoginEmailRequest;
import com.lucky.luckyojbackendmodel.model.dto.user.UserQueryRequest;
import com.lucky.luckyojbackendmodel.model.entity.User;
import com.lucky.luckyojbackendmodel.model.vo.LoginUserVO;
import com.lucky.luckyojbackendmodel.model.vo.UserVO;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 用户服务
 *

 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 更新 secretKey
     *
     * @param id 用户id
     * @return boolean
     */
    Boolean updateSecretKey(Long id);

    /**
     * 更新密码
     * @param originalPassword
     * @param newPassword
     * @return
     */
    Boolean updatePassword(String originalPassword,String newPassword,HttpServletRequest httpServletRequest);
    /**
     * 发送邮箱验证码
     *
     * @param email
     * @return
     * @throws MessagingException
     */
    Boolean sendMsg(String email) throws UnsupportedEncodingException, AddressException;

    /**
     * 使用邮箱登陆
     *
     * @param loginEmailRequest
     * @param httpSession
     * @return
     */
    User loginByEmail(LoginEmailRequest loginEmailRequest, HttpSession httpSession);

    /**
     * 使用邮箱注册账号
     * @param email
     * @param userPassword
     * @param checkPassword
     * @param code
     * @return
     */
    Long registerByEmail(String email, String userPassword, String checkPassword, String code);

    /**
     * 更新邮箱信息
     * @param email
     * @param code
     * @param httpServletRequest
     */
    void updateMail(String email,String code ,HttpServletRequest httpServletRequest);
}
