package cn.deru.backend.service.admin;

import cn.deru.backend.dto.PostDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.mapper.PostMapper;
import cn.deru.backend.model.Post;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdminPostService {

    @Autowired
    private PostMapper postMapper;

    /**
     * 分页查询所有帖子（升序）
     */
    public IPage<PostDTO> getPosts(Integer page, Integer pageSize) {
        Page<PostDTO> postPage = new Page<>(page, pageSize);
        return postMapper.selectAllPostsAdminAsc(postPage);
    }

    /**
     * 删除帖子（软删除）
     */
    public void deletePost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getIsDeleted() == 1) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        post.setIsDeleted(1);
        post.setUpdateTime(new Date());
        postMapper.updateById(post);
    }
}