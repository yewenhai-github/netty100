package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.Issue;
import com.netty100.pojo.dto.IssuePageQueryDto;

/**
 * @author why
 */
public interface IssueService {

    boolean save(Issue issue);

    boolean update(Issue issue);

    boolean deleteById(Long id);

    PageInfo<Issue> getPage(IssuePageQueryDto dto);

}
