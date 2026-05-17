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
@TableName("journals")
public class Journal {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer jid;
    private Integer typeid;
    private String coverPath;
    private String title;
    private String department;
    private String organizer;
    private String editorialOffice;
    private String address;
    private String postalCode;
    private String phone;
    private String email;
    private String emailIssue;
    private String website;
    private String cnNumber;
    private String issn;
    private String postalCodeSubscription;
    private String price;
    private String compositeImpactFactor;
    private String comprehensiveImpactFactor;
    private String introduction;
    private java.sql.Timestamp createdAt;
}
