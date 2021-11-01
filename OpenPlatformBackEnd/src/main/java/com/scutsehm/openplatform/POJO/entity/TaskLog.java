package com.scutsehm.openplatform.POJO.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * task 的日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TaskLog {
    /**
     * 对应着trainTask或processTask 的id
     */
    @Id
    private String taskId;

    /**
     * 日志的具体内容
     */
//    @Column()
    @Lob
    private String content;
}
