package com.heima.user.Controller.v1;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.Service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/login")
@Api(value = "app端用户登录", tags = "ap_user", description = "app端用户登录API")
public class ApUserLoginController {
    @Autowired
    private ApUserService apUserService;

    /**
     * 用户登录
     * @param dto
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login_auth")
    public ResponseResult login (@RequestBody LoginDto dto){
        log.info("用户登录,{}",dto);
        return apUserService.login(dto);
    }
}
