package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.Issue;
import com.netty100.mapper.IssueMapper;
import com.netty100.pojo.dto.IssuePageQueryDto;
import com.netty100.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author why
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IssueServiceImpl implements IssueService {

    private final IssueMapper issueMapper;

    @Override
    public boolean save(Issue issue) {
        return issueMapper.save(issue);
    }

    @Override
    public boolean update(Issue issue) {
        return issueMapper.update(issue);
    }

    @Override
    public boolean deleteById(Long id) {
        return issueMapper.deleteById(id);
    }

    private List<Issue> getList(IssuePageQueryDto dto) {
        return issueMapper.getList(dto);
    }

    @Override
    public PageInfo<Issue> getPage(IssuePageQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        return new PageInfo<>(getList(dto));
    }
}
