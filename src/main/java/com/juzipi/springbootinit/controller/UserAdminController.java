package com.juzipi.springbootinit.controller;

import co.elastic.clients.elasticsearch.xpack.usage.Base;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzipi.springbootinit.annotation.AuthCheck;
import com.juzipi.springbootinit.common.BaseResponse;
import com.juzipi.springbootinit.common.ErrorCode;
import com.juzipi.springbootinit.common.ResultUtils;
import com.juzipi.springbootinit.constant.UserConstant;
import com.juzipi.springbootinit.exception.BusinessException;
import com.juzipi.springbootinit.exception.ThrowUtils;
import com.juzipi.springbootinit.mapper.ForbiddataMapper;
import com.juzipi.springbootinit.model.dto.forbidWord.ForbidWordAddRequest;
import com.juzipi.springbootinit.model.dto.forbidWord.ForbidWordRemoveRequest;
import com.juzipi.springbootinit.model.dto.forbidWord.ForbidWordSelectRequest;
import com.juzipi.springbootinit.model.dto.forbidWord.ForbidWordUpdateRequest;
import com.juzipi.springbootinit.model.dto.user.UserAddRequest;
import com.juzipi.springbootinit.model.dto.user.UserAdminAddRequest;
import com.juzipi.springbootinit.model.dto.user.UserLoginRequest;
import com.juzipi.springbootinit.model.dto.user.UserQueryRequest;
import com.juzipi.springbootinit.model.entity.Chatmessage;
import com.juzipi.springbootinit.model.entity.Forbiddata;
import com.juzipi.springbootinit.model.entity.User;
import com.juzipi.springbootinit.model.enums.UserRoleEnum;
import com.juzipi.springbootinit.model.vo.LoginUserVO;
import com.juzipi.springbootinit.service.ChatmessageService;
import com.juzipi.springbootinit.service.ForbiddataService;
import com.juzipi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserChatController
 * @Description: 用户管理
 * @Author: 橘子皮
 * @CreateDate: 2025/3/7 15:49
 */
@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserAdminController {

    @Resource
    private ChatmessageService chatMessageService;

    @Resource
    private UserService userService;

    @Resource
    private ForbiddataService forbiddataService;

    @Resource
    private ForbiddataMapper forbiddataMapper;

    //region 违禁词库相关

    /**
     * 增加
     *
     * @param forbidWordAddRequest
     * @return
     */
    @PostMapping("/addWord")
    public BaseResponse<Long> addWord(@RequestBody ForbidWordAddRequest forbidWordAddRequest) {
        if (forbidWordAddRequest == null || StringUtils.isBlank(forbidWordAddRequest.getForbidWord())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请输入正确的违禁词");
        }
        Forbiddata forbiddata = new Forbiddata();
        forbiddata.setForbidWord(forbidWordAddRequest.getForbidWord());
        forbiddataMapper.insert(forbiddata);
        return ResultUtils.success(forbiddata.getId());
    }

    /**
     * 更新
     *
     * @param forbidWordUpdateRequest
     * @return
     */
    @PostMapping("/updateWord")
    public BaseResponse<Boolean> updateWord(@RequestBody ForbidWordUpdateRequest forbidWordUpdateRequest) {
        if (forbidWordUpdateRequest == null || StringUtils.isBlank(forbidWordUpdateRequest.getForbidWord())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请输入完整的参数");
        }
        Integer id = forbidWordUpdateRequest.getId();
        Forbiddata forbiddata = forbiddataMapper.selectById(id);
        if (forbiddata != null) {
            forbiddata.setForbidWord(forbidWordUpdateRequest.getForbidWord());
            forbiddataMapper.updateById(forbiddata);
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/deleteWord")
    public BaseResponse<Boolean> deleteWord(@RequestBody ForbidWordRemoveRequest forbidWordRemoveRequest) {
        if (forbidWordRemoveRequest == null || ObjectUtils.isEmpty(forbidWordRemoveRequest.getId())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请输入完整的参数");
        }
        forbiddataMapper.deleteById(forbidWordRemoveRequest.getId());
        return ResultUtils.success(true);
    }


    @PostMapping("/selectWord")
    public BaseResponse<List<Forbiddata>> updateWord(@RequestBody ForbidWordSelectRequest forbidWordSelectRequest) {
        if (forbidWordSelectRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请输入完整的参数");
        }
        LambdaQueryWrapper<Forbiddata> queryWrapper = new LambdaQueryWrapper<>();
        if (forbidWordSelectRequest.getId() != null) {
            queryWrapper.eq(Forbiddata::getId, forbidWordSelectRequest.getId());
        }
        if (forbidWordSelectRequest.getForbidWord() != null) {
            queryWrapper.like(Forbiddata::getForbidWord, forbidWordSelectRequest.getForbidWord());
        }
        List<Forbiddata> forbiddata = forbiddataMapper.selectList(queryWrapper);
        if (forbiddata.isEmpty()) {
            return ResultUtils.success(new ArrayList<Forbiddata>());
        }
        return ResultUtils.success(forbiddata);
    }
    //endregion


    /**
     * 查询所有消息记录
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<Chatmessage>> chatListMessage() {
        List<Chatmessage> list = chatMessageService.list();
        return ResultUtils.success(list);
    }


    /**
     * 用户登录 (管理员端)
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/add/user")
    public BaseResponse<Long> addUser(@RequestBody UserAdminAddRequest userAdminAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = userService.addAdminUser(userAdminAddRequest, request);
        return ResultUtils.success(userId);
    }


    @GetMapping("/upgrade/role")
    public BaseResponse<String> upgradeRole(@RequestParam String userId, @RequestParam String role, HttpServletRequest request) {
        //1.先判断当前登录用户是否是 管理员，
        User loginUser = userService.getLoginUser(request);
        boolean admin = userService.isAdmin(loginUser);
        if (!admin) {
            return ResultUtils.error(404, "没有登录或者无权操作");
        }

        // 2. 检查目标用户是否存在
        User targetUser = userService.getById(userId);
        if (targetUser == null) {
            return ResultUtils.error(406, "用户未找到");
        }

        // 3. 校验角色是否合法
        if (!UserRoleEnum.isValidRole(role)) {
            return ResultUtils.error(400, "非法的角色类型");
        }

        // 4. 更新用户角色
        targetUser.setUserRole(role);
        boolean result = userService.updateById(targetUser);
        if (!result) {
            return ResultUtils.error(500, "用户角色更新失败，请稍后重试");
        }

        return ResultUtils.success("修改成功");
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }


}
