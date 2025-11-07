package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constant.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import org.apache.avro.data.Json;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

public class TaskServiceImpl implements TaskService {
    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    @Override
    public long addTask(Task task) {
        //增加任务到数据库
        boolean success = addTaskToDb(task);
        //如果添加成功,将任务添加到redis
        if(success){
            addTaskToCache(task);
        }
        //返回task的id
        return task.getTaskId();

    }
    @Autowired
    private CacheService cacheService;

    private void addTaskToCache(Task task) {
        //获取五分钟后的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        long nextScheduleTime = calendar.getTimeInMillis();
        //获取key
        String key = task.getTaskType() + "_" + task.getPriority();

//        根据任务时间决定储存在list或者zset
        if(task.getExecuteTime() <= System.currentTimeMillis()){
            //储存到list
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        }else if (task.getExecuteTime() <= nextScheduleTime){
            cacheService.zAdd(ScheduleConstants.FUTURE + key,JSON.toJSONString(task),task.getExecuteTime());
        }
    }

    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    private boolean addTaskToDb(Task task) {
        boolean flag = false;
        try {
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task,taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            //将数据保存到taskInfo表中
            taskinfoMapper.insert(taskinfo);

            //保存任务日志到数据库中
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(task,taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);
            flag = true;
        } catch (BeansException e) {
            e.printStackTrace();
        }

        return flag;

    }
}
