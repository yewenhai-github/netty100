package com.netty100.mapper;

import com.netty100.entity.Issue;
import com.netty100.pojo.dto.IssuePageQueryDto;

import java.util.List;

/**
 * @author why
 */
public interface IssueMapper {

    boolean save(Issue issue);

    boolean update(Issue issue);

    boolean deleteById(Long id);

    List<Issue> getList(IssuePageQueryDto dto);
}
