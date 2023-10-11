package com.lucky.luckyojbackenduserservice.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lucky.luckyojbackendcommon.common.CustomException;
import com.lucky.luckyojbackendcommon.common.ErrorCode;
import com.lucky.luckyojbackendcommon.constant.CommonConstant;
import com.lucky.luckyojbackendcommon.exception.BusinessException;
import com.lucky.luckyojbackendcommon.utils.SqlUtils;
import com.lucky.luckyojbackendmodel.model.dto.email.LoginEmailRequest;
import com.lucky.luckyojbackendmodel.model.dto.user.UserQueryRequest;
import com.lucky.luckyojbackendmodel.model.entity.User;
import com.lucky.luckyojbackendmodel.model.enums.UserRoleEnum;
import com.lucky.luckyojbackendmodel.model.vo.LoginUserVO;
import com.lucky.luckyojbackendmodel.model.vo.UserVO;
import com.lucky.luckyojbackenduserservice.mapper.UserMapper;
import com.lucky.luckyojbackenduserservice.service.UserService;
import com.lucky.luckyojbackenduserservice.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lucky.luckyojbackendcommon.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现
 *

 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "lucky";

    /**
     * 时间标志
     */
    private static final String TIME_SIGN = "Time_sign";

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey，secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserName(StringUtils.upperCase(userAccount));
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAvatar("https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/Multiavatar-f5871c303317a4dafbf6.png");
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String email = userQueryRequest.getEmail();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Boolean updateSecretKey(Long id) {
        User user = this.getById(id);
        String accessKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(8));
        user.setSecretKey(secretKey);
        user.setAccessKey(accessKey);
        return this.updateById(user);
    }

    @Override
    public Boolean updatePassword(String originalPassword, String newPassword,HttpServletRequest httpServletRequest) {
        // 获取当前登陆用户
        User loginUser = getLoginUser(httpServletRequest);
        // 获取当前用户的密码
        String userPassword = loginUser.getUserPassword();
        // 对旧密码进行加密比对
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + originalPassword).getBytes());
        if (!userPassword.equals(encryptPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"旧密码错误，请重新输入");
        }
        // 对新密码进行加密，存入用户
        String newPsw = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserPassword(newPsw);

        return this.updateById(user);
    }

    @Override
    public Boolean sendMsg(String email) throws UnsupportedEncodingException, AddressException {
        String redisCode = null;
        //如果邮箱不为空
        if (email.isEmpty()) {
            // 获取Redis中的code以判断是否发送过验证码
            redisCode = (String) redisTemplate.opsForValue().get(email);
        }
        // 判断是否发送过时间标志,判断用户是否在60秒内发送过验证码
        if (!StringUtils.isEmpty((String) redisTemplate.opsForValue().get(TIME_SIGN))) {
            throw new BusinessException(ErrorCode.CODE_EXCEPTION);
        }
        // 随机生成一个6位数验证码
        String code = MailUtils.getCode();
        log.info("code:{}", code);
        //发送验证码给邮箱
//        MailUtils.sendTestMail(email, code);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("LuckyOJ项目验证码");
        simpleMailMessage.setText("尊敬的用户：" + email + "，您的验证码为：" + code + "，有效期为5分钟，请妥善保管");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(new InternetAddress(MimeUtility.encodeText("LuckyOJ")+"															<815845992@qq.com>").toString());
        try {
            javaMailSender.send(simpleMailMessage);
            //把获得的验证码存入session中保存作用域，方便后面拿出来比对
//            session.setAttribute(email, code);
            // 将生成的验证码缓存到Redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
            // 在检验码缓存的同时缓存，缓存一个时间标志
            redisTemplate.opsForValue().set(TIME_SIGN, code, 60,TimeUnit.SECONDS);
            log.info("邮件发送成功");
        }
        catch (Exception e) {
            log.info("邮件发送出现异常");
        }
        return true;
    }

    @Override
    public User loginByEmail(LoginEmailRequest loginEmailRequest, HttpSession session) {
        //获取前端传过来的用户信息
        String email = loginEmailRequest.getEmail();
        String code = loginEmailRequest.getCode();
        //验证邮箱和验证码是否为空，如果为空直接登陆失败
        if (email.isEmpty() || code.isEmpty()) {
            throw new CustomException("邮箱或验证码不能为空");
        }
        //如果邮箱和验证码不为空，判断数据库是否存在该用户
        // 获取之前存在session中保存作用域中的正确验证码
//        String trueCode = (String) session.getAttribute(email);
        // 从Redis中获取缓存的验证码
        String trueCode = (String) redisTemplate.opsForValue().get(email);

        // 对比用户输入的验证码
        if (!code.equals(trueCode)) {
            throw new BusinessException(ErrorCode.WRONG_CODE,"验证码错误");
        }

        // 如果验证码匹配，开始调用数据库查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = this.getOne(queryWrapper);

        // 如果数据库中没有该用户，将信息添加到数据库（添加新用户）
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_EXIST,"用户不存在,请注册！");
//            // 2. 将邮件设置为用户名
//            String userAccount = email;
//            // 添加新用户
//            user = new User();
//            user.setUserAccount(userAccount);
//            user.setUserName(StringUtils.upperCase(userAccount));
//            user.setEmail(email);
//            this.save(user);
        }
        // 把登陆用户存储在session中，表示已经登陆
        session.setAttribute(USER_LOGIN_STATE, user);

        // 如果登陆成功，删除Redis中缓存的验证码
        redisTemplate.delete(email);
        return user;
    }

    @Override
    public Long registerByEmail(String email, String userPassword, String checkPassword, String code) {
        // 1. 校验
        if (StringUtils.isAnyBlank(email, userPassword, checkPassword,code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 从Redis中获取缓存的验证码
        String trueCode = (String) redisTemplate.opsForValue().get(email);

        // 对比用户输入的验证码
        if (!code.equals(trueCode)) {
            throw new CustomException("验证码错误");
        }

        synchronized (email.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email);
            queryWrapper.eq("userAccount", email);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 将邮件设置为用户名
            String userAccount = email;
            // 3. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 4. 分配 accessKey，secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 5. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserName(StringUtils.upperCase(userAccount));
            user.setUserPassword(encryptPassword);
            user.setEmail(email);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAvatar("https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/Multiavatar-f5871c303317a4dafbf6.png");
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            // 如果登陆成功，删除Redis中缓存的验证码
            redisTemplate.delete(email);
            return user.getId();
        }
    }

    @Override
    public void updateMail(String email, String code, HttpServletRequest httpServletRequest) {
        // 根据httpServletRequest获取用户id
        User loginUser = this.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();

        // 从Redis中获取缓存的验证码
        String trueCode = (String) redisTemplate.opsForValue().get(email);

        // 对比用户输入的验证码
        if (!StringUtils.isEmpty(trueCode) && code.equals(trueCode)) {
            // 删除redis中的验证码
            redisTemplate.delete(trueCode);
            
            User user= this.getById(userId);
            user.setEmail(email);
            // 更新数据库
            updateById(user);
        }
        else {
            throw new BusinessException(ErrorCode.WRONG_CODE,"验证码错误或不存在");
        }
    }

}
