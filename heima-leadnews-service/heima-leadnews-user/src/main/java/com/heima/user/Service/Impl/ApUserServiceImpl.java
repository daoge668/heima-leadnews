package com.heima.user.Service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.Mapper.ApUserMapper;
import com.heima.user.Service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.Wrapper;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper,ApUser> implements ApUserService {
    /**
     * 登录接口
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        if (!StringUtil.isBlank(dto.getPhone())&&!StringUtil.isBlank(dto.getPassword())){
            ApUser apUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            //查看是否有此用户
            if(apUser == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
            }
            //如果存在,比对密码
            String salt = apUser.getSalt();
            String password = dto.getPassword();
            password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!password.equals(apUser.getPassword())){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //返回数据
            Map<String,Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(apUser.getId().longValue()));
            apUser.setPassword("");
            apUser.setSalt("");
            map.put("user",apUser);
            return ResponseResult.okResult(map);
        }else{
            Map<String,Object> map = new HashMap<>();
            map.put("token",01);
            return ResponseResult.okResult(map);
        }
    }
}
