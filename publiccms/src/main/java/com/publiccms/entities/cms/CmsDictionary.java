package com.publiccms.entities.cms;
// Generated 2016-11-20 14:46:17 by Hibernate Tools 4.3.1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sanluan.common.source.annotation.GeneratorColumn;

/**
 * CmsDictionary generated by hbm2java
 */
@Entity
@Table(name = "cms_dictionary")
public class CmsDictionary implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @GeneratorColumn(title = "ID")
    private Long id;
    @GeneratorColumn(title = "名称")
    private String name;
    @GeneratorColumn(title = "允许多选", condition = true)
    private boolean multiple;

    public CmsDictionary() {
    }

    public CmsDictionary(String name, boolean multiple) {
        this.name = name;
        this.multiple = multiple;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)

    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "multiple", nullable = false)
    public boolean isMultiple() {
        return this.multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

}