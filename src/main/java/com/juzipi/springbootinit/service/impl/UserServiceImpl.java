package com.juzipi.springbootinit.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juzipi.springbootinit.common.ErrorCode;
import com.juzipi.springbootinit.config.WxConfigProperties;
import com.juzipi.springbootinit.constant.CommonConstant;
import com.juzipi.springbootinit.constant.UserConstant;
import com.juzipi.springbootinit.exception.BusinessException;
import com.juzipi.springbootinit.mapper.UserMapper;
import com.juzipi.springbootinit.model.dto.user.UserAdminAddRequest;
import com.juzipi.springbootinit.model.dto.user.UserMiniLoginRequest;
import com.juzipi.springbootinit.model.dto.user.UserQueryRequest;
import com.juzipi.springbootinit.model.dto.user.UserWxMiniDto;
import com.juzipi.springbootinit.model.entity.User;
import com.juzipi.springbootinit.model.enums.UserRoleEnum;
import com.juzipi.springbootinit.model.vo.LoginUserVO;
import com.juzipi.springbootinit.model.vo.UserVO;
import com.juzipi.springbootinit.service.UserService;
import com.juzipi.springbootinit.utils.PhoneNumberDecrypter;
import com.juzipi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.juzipi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${server.servlet.session.cookie.max-age}")
    private int expireTime;

    @Resource
    private WxConfigProperties wxConfigProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "juzipi";
    @Autowired
    private UserMapper userMapper;

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
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
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

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    @Override
    public SaTokenInfo userLoginByWxMN(UserMiniLoginRequest userMiniLoginRequest) {
        String code = userMiniLoginRequest.getCode();
        UserWxMiniDto userWxMiniDto = getWxDtoMessage(code);
        // 单机锁
        synchronized (code.intern()) {
            String openId = userWxMiniDto.getOpenid();
            String sessionKey = userWxMiniDto.getSession_key();
            if (openId == null || sessionKey == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录失败");
            }

            //获取解密手机号
            String iv = userMiniLoginRequest.getIv();
            String encryptedData = userMiniLoginRequest.getEncryptedData();
            String phone = PhoneNumberDecrypter.decryptPhoneNumber(sessionKey, encryptedData, iv);
            if (StringUtils.isBlank(phone)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            //查询用户是否存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mpOpenId", openId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            //注册逻辑
            if (user == null) {
                //如果用户不存在，则创建用户
                user = new User();
                user.setUserAccount(phone);
                user.setPhone(phone);
                String userPassword = "123456";
                user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes()));
                user.setMpOpenId(openId);
                boolean save = this.save(user);
                if (!save) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }

            StpUtil.login(user.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            String tokenValue = StpUtil.getTokenValue();
            Gson gson = new Gson();
            String tokenInfoStr = gson.toJson(tokenInfo);
            String redisKey = USER_LOGIN_STATE + ":" + tokenValue;
            String result = stringRedisTemplate.opsForValue().get(redisKey);
            if (result == null) {
                stringRedisTemplate.opsForValue().set(redisKey, tokenInfoStr);
            }
            return tokenInfo;
        }

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
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
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
        if (CollUtil.isEmpty(userList)) {
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
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public Long addAdminUser(UserAdminAddRequest userAdminAddRequest, HttpServletRequest request) {
        String userName = userAdminAddRequest.getUserName();
        String userAccount = userAdminAddRequest.getUserAccount();
        String userPassword = userAdminAddRequest.getUserPassword();
        String userRole = userAdminAddRequest.getUserRole();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User selectUser = userMapper.selectOne(queryWrapper);
        if (selectUser != null) {
            return 0l;
        }

        User user = new User();
        if (StringUtils.isNotBlank(userName)) {
            user.setUserName(userName);
        }
        if (StringUtils.isNotBlank(userAccount)) {
            user.setUserAccount(userAccount);
        }
        if (StringUtils.isNotBlank(userPassword)) {
            user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes()));
        }
        if (StringUtils.isNotBlank(userRole)) {
            user.setUserRole(userRole);
        }
        int insert = userMapper.insert(user);
        return (long) insert;
    }

    @Override
    public User getLoginUserNoStatus(String tokenValue) {
        String redisKey = USER_LOGIN_STATE + ":" + tokenValue;
        String tokenInfoStr = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(tokenInfoStr)) {
            return null;
        }
        Gson gson = new Gson();
        SaTokenInfo saTokenInfo = gson.fromJson(tokenValue, SaTokenInfo.class);
        Long loginId = (Long) saTokenInfo.getLoginId();
        return this.getById(loginId);
    }

    /**
     * 生成随机数
     *
     * @return
     */
    public static String generateSixDigitRandomNumber() {
        // 生成 100000 到 999999 之间的随机数
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(randomNumber);
    }

    /**
     * 通过微信登录code获取openId
     *
     * @param code 微信登录code
     * @return openId
     */
    public UserWxMiniDto getWxDtoMessage(String code) {
        // 添加参数
        String appId = wxConfigProperties.getAppId();
        String secret = wxConfigProperties.getSecret();
        String authorization_code = "authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=" + authorization_code;
        String respJson = restTemplate.getForObject(url, String.class);
        Gson gson = new Gson();
        return gson.fromJson(respJson, UserWxMiniDto.class);

    }
}
