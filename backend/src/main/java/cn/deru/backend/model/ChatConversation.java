package cn.deru.backend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("chat_conversation")
public class ChatConversation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long adminId;

    private String lastMessage;

    private LocalDateTime lastTime;

    private Integer unreadUser;

    private Integer unreadAdmin;

    private LocalDateTime createdAt;
}
