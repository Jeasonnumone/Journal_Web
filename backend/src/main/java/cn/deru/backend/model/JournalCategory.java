package cn.deru.backend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("journal_categories")
public class JournalCategory {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer typeid;
    private String name;
    private Integer parentId;
    private java.sql.Timestamp createdAt;
}
