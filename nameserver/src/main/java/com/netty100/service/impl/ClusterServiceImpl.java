package com.netty100.service.impl;

import cn.hutool.core.date.DateUtil;
import com.netty100.entity.Cluster;
import com.netty100.entity.User;
import com.netty100.entity.UserCluster;
import com.netty100.enumeration.ServerStatus;
import com.netty100.enumeration.UserType;
import com.netty100.mapper.ClusterMapper;
import com.netty100.pojo.dto.AddClusterDto;
import com.netty100.pojo.dto.AssignClusterToUserDto;
import com.netty100.pojo.dto.UpdateClusterDto;
import com.netty100.pojo.vo.ClusterVo;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.pojo.vo.UserClusterVo;
import com.netty100.service.*;
import com.netty100.utils.SecurityContext;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
@Service
public class ClusterServiceImpl implements ClusterService {

    private ClusterMapper clusterMapper;

    private UserService userService;

    private ServerService serverService;

    private ReportMinuteDataService reportMinuteDataService;

    private UserClusterService userClusterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Cluster cluster) {
        return clusterMapper.save(cluster);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateClusterDto dto) {
        return clusterMapper.update(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Cluster getById(Integer clusterId) {
        return clusterMapper.getById(clusterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(AssignClusterToUserDto dto) {
        val clusterId = dto.getClusterId();
        val userIds = dto.getUserIds();
        val cluster = this.getById(clusterId);
        if (Objects.isNull(cluster)) {
            throw new CommonException(ResponseMsg.CLUSTER_NOT_EXIST);
        }
        //移除之前分配的负责人
        userClusterService.delete(clusterId);
        val userIdArr = userIds.split(",");
        val list = new ArrayList<UserCluster>(userIdArr.length);
        Arrays.stream(userIdArr).forEach(arg -> {
            val id = Integer.valueOf(arg);
            final User user = userService.getById(id);
            if (Objects.nonNull(user)) {
                val userCluster = new UserCluster();
                userCluster.setUserId(id);
                userCluster.setClusterId(clusterId);
                userCluster.setCreateDate(new Date());
                list.add(userCluster);
            }
        });
        userClusterService.assign(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Cluster getByClusterName(String clusterName) {
        return clusterMapper.getByClusterName(clusterName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClusterVo> getClusters() {
        List<Cluster> list = this.getClusterInfo();
        if (Objects.isNull(list)) {
            return null;
        }
        List<ClusterVo> voList = new ArrayList<>(list.size());
        list.forEach(entity -> {
            val clusterVo = new ClusterVo();
            BeanUtils.copyProperties(entity, clusterVo);
            final List<ServerVo> servers = serverService.getServers(entity.getId());
            servers.forEach(serverVo -> {
                if (serverVo.getServerStatus() == ServerStatus.DOWN) {
                    serverVo.setClientConnectCount(0);
                    serverVo.setServerConnectCount(0);
                }
//                else {
//                    val data = reportMinuteDataService.getLatest(serverVo.getId());
//                    int serverConnectCount = Objects.isNull(data) ? 0 : Objects.isNull(data.getServerConnectCount()) ? 0 : data.getServerConnectCount();
//                    int clientConnectCount = Objects.isNull(data) ? 0 : Objects.isNull(data.getClientConnectCount()) ? 0 : data.getClientConnectCount();
//                    serverVo.setServerConnectCount(serverConnectCount);
//                    serverVo.setClientConnectCount(clientConnectCount);
//                }
            });
            clusterVo.setUpServerCount((int) servers.parallelStream().filter(x -> x.getServerStatus() == ServerStatus.UP).count());
            clusterVo.setTotalServerCount(servers.size());
            clusterVo.setClientConnectionCountTotal(servers.parallelStream().map(ServerVo::getClientConnectCount).reduce(0, Integer::sum));
            clusterVo.setServerConnectionCountTotal(servers.parallelStream().map(ServerVo::getServerConnectCount).reduce(0, Integer::sum));
            voList.add(clusterVo);
        });
        return voList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cluster> getClusterInfo() {
        List<Cluster> list;
        val user = SecurityContext.getUser();
        if (user.getUserType() == UserType.ADMIN) {
            list = clusterMapper.getAll();
        } else {
            final List<Integer> clusterIds = userClusterService.getClusterIdsByUserId(user.getId());
            //该用户没有负责的集群
            if (clusterIds.isEmpty()) {
                return null;
            }
            list = clusterMapper.getByIds(clusterIds);
        }
        return list;
    }

    @Override
    public List<Integer> getByUserId(Integer userId) {
        return userClusterService.getClusterIdsByUserId(userId);
    }

    @Override
    public List<Integer> getIds() {
        return clusterMapper.getIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserClusterVo> getUserClusters() {
        List<UserClusterVo> result = new ArrayList<>();
        List<UserCluster> userClusters = userClusterService.getAll();
        val format = "yyyy-MM-dd HH:mm:ss.SSS";
        val map = new HashMap<String, List<Integer>>(16);
        userClusters.forEach(userClusterVo -> {
            val clusterId = userClusterVo.getClusterId();
            val clusterName = this.getById(clusterId).getCluster();
            map.compute(clusterName + "@" + clusterId.toString() + "@" + DateUtil.format(userClusterVo.getCreateDate(), format), (k, v) -> {
                if (Objects.isNull(v)) {
                    v = new ArrayList<>();
                }
                v.add(userClusterVo.getUserId());
                return v;
            });
        });
        map.forEach((k, v) -> {
            val vo = new UserClusterVo();
            val keyArr = k.split("@");
            val key = new UserClusterVo.ClusterVo2();
            key.setCluster(keyArr[0]);
            key.setClusterId(Integer.parseInt(keyArr[1]));
            key.setCreateTime(DateUtil.parse(keyArr[2], format));
            vo.setClusterVo2(key);
            val values = new ArrayList<UserClusterVo.UserVo>();
            v.forEach(x -> {
                val value = new UserClusterVo.UserVo();
                value.setUserId(x);
                User user = userService.getById(x);
                if(null != user){
                    value.setUsername(userService.getById(x).getUsername());
                }
                values.add(value);
            });
            vo.setUserVoList(values);
            result.add(vo);
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCluster(AddClusterDto dto) {
        //集群名称不能相同
        Cluster cluster = this.getByClusterName(dto.getCluster());
        if (Objects.nonNull(cluster)) {
            throw new CommonException(ResponseMsg.CLUSTER_ALREADY_EXIST);
        }
        cluster = new Cluster();
        cluster.setCluster(dto.getCluster());
        cluster.setDescription(dto.getDescription());
        this.save(cluster);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCluster(UpdateClusterDto dto) {
        if (StringUtils.hasText(dto.getCluster())) {
            //集群名称不能相同
            Cluster cluster = this.getByClusterName(dto.getCluster());
            if (Objects.nonNull(cluster) && !Objects.equals(dto.getId(), cluster.getId())) {
                throw new CommonException(ResponseMsg.CLUSTER_ALREADY_EXIST);
            }
        }
        this.update(dto);
    }


    @Autowired
    public void setClusterMapper(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setReportMinuteDataService(ReportMinuteDataService reportMinuteDataService) {
        this.reportMinuteDataService = reportMinuteDataService;
    }

    @Autowired
    public void setUserClusterService(UserClusterService userClusterService) {
        this.userClusterService = userClusterService;
    }

    @Override
    public List<Cluster> queryAll() {
        return clusterMapper.getAll();
    }
}
