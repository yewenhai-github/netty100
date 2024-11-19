package com.netty100.controller.admin;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.Issue;
import com.netty100.pojo.dto.IssuePageQueryDto;
import com.netty100.service.IssueService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author why
 */
@RestController
@RequestMapping(value = "/admin/issue")
@Api(tags = "【页面功能-用户手册-疑难杂症】")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IssueController {

    private final IssueService issueService;

    @ApiOperation(value = "新增")
    @PostMapping(value = "/add")
    public WebResult<Boolean> add(@RequestBody Issue issue) {
        return WebResult.ok(issueService.save(issue));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/remove/{id}")
    public WebResult<Boolean> remove(@PathVariable(value = "id") Long id) {
        return WebResult.ok(issueService.deleteById(id));
    }

    @ApiOperation(value = "更新")
    @PostMapping(value = "/update")
    public WebResult<Boolean> update(@RequestBody Issue issue) {
        return WebResult.ok(issueService.update(issue));
    }

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/query/page")
    public WebResult<PageInfo<Issue>> getPage(@RequestBody IssuePageQueryDto dto) {
        return WebResult.ok(issueService.getPage(dto));
    }

}
